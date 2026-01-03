package com.internshipplatform.internshipplatform.service;

import com.internshipplatform.internshipplatform.dto.MatchScoreDTO;
import com.internshipplatform.internshipplatform.dto.ScoreBreakdownDTO;
import com.internshipplatform.internshipplatform.entity.Internship;
import com.internshipplatform.internshipplatform.entity.Student;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScoringService {

    // Weights
    private static final int W_REQUIRED = 50;
    private static final int W_PREFERRED = 20;
    private static final int W_DEGREE = 10;
    private static final int W_EXPERIENCE = 10;
    private static final int W_EXTRAS = 10;

    // Tuned caps
    private static final int CAP_DEGREE_TOO_LOW = 65;
    private static final int CAP_EXPERIENCE_TOO_LOW = 70;
    private static final int CAP_BOTH_TOO_LOW = 55;
    private static final int CAP_NO_SKILLS = 25;

    public MatchScoreDTO score(Student student, Internship internship) {

        // Student skills
        Set<String> studentCore = parseCsv(student.getCoreSkills());
        Set<String> studentOther = parseCsv(student.getOtherSkills());

        // Backward compatibility: merge legacy "skills" into OTHER
        Set<String> legacySkills = parseCsv(student.getSkills());
        if (!legacySkills.isEmpty()) studentOther.addAll(legacySkills);

        boolean studentHasAnySkill = !studentCore.isEmpty() || !studentOther.isEmpty();

        // Internship skills
        Set<String> required = parseCsv(internship.getRequiredSkills());
        Set<String> preferred = parseCsv(internship.getPreferredSkills());

        // ---- Flags + caps
        List<String> flags = new ArrayList<>();
        Integer capApplied = null;

        boolean degreeTooLow = isBelow(student.getDegreeLevel(), internship.getMinimumDegreeLevel());
        boolean expTooLow = isBelow(student.getExperienceLevel(), internship.getMinimumExperienceLevel());

        if (!studentHasAnySkill) {
            flags.add("NO_SKILLS_PROFILE");
            capApplied = CAP_NO_SKILLS;
        }

        if (internship.getMinimumDegreeLevel() != null && degreeTooLow) {
            flags.add("DEGREE_TOO_LOW");
        }
        if (internship.getMinimumExperienceLevel() != null && expTooLow) {
            flags.add("EXPERIENCE_TOO_LOW");
        }

        if (internship.getMinimumDegreeLevel() != null && internship.getMinimumExperienceLevel() != null
                && degreeTooLow && expTooLow) {
            capApplied = minCap(capApplied, CAP_BOTH_TOO_LOW);
        } else {
            if (internship.getMinimumDegreeLevel() != null && degreeTooLow) {
                capApplied = minCap(capApplied, CAP_DEGREE_TOO_LOW);
            }
            if (internship.getMinimumExperienceLevel() != null && expTooLow) {
                capApplied = minCap(capApplied, CAP_EXPERIENCE_TOO_LOW);
            }
        }

        // ---- Skill scoring (explicit methods)
        WeightedMatch requiredMatch = weightedMatch(required, studentCore, studentOther);
        int requiredScore = computeRequiredSkillScore(required, requiredMatch.weightedCount, flags);

        WeightedMatch preferredMatch = weightedMatch(preferred, studentCore, studentOther);
        int preferredScore = computePreferredSkillScore(preferred, preferredMatch.weightedCount);

        // ---- Degree (0-10)
        int degreeScore = computeLevelScore(
                internship.getMinimumDegreeLevel(),
                student.getDegreeLevel(),
                W_DEGREE
        );

        // ---- Experience (0-10)
        int experienceScore = computeLevelScore(
                internship.getMinimumExperienceLevel(),
                student.getExperienceLevel(),
                W_EXPERIENCE
        );

        // ---- Extras (0-10)
        int extrasScore = computeExtrasScore(student);

        int raw = requiredScore + preferredScore + degreeScore + experienceScore + extrasScore;
        int finalScore = (capApplied != null) ? Math.min(raw, capApplied) : raw;
        finalScore = clamp(finalScore, 0, 100);

        // Missing required skills list (simple, non-weighted)
        List<String> matchedRequired = requiredMatch.matched;
        List<String> missingRequired = required.stream()
                .filter(r -> !matchedRequired.contains(r))
                .sorted()
                .toList();

        return MatchScoreDTO.builder()
                .score(finalScore)
                .capApplied(capApplied)
                .flags(flags)
                .breakdown(ScoreBreakdownDTO.builder()
                        .requiredSkills(requiredScore)
                        .preferredSkills(preferredScore)
                        .degree(degreeScore)
                        .experience(experienceScore)
                        .extras(extrasScore)
                        .build())
                .matchedRequiredSkills(matchedRequired)
                .missingRequiredSkills(missingRequired)
                .build();
    }

    // ---------------- main scoring helpers ----------------

    /**
     * Required skills: 0..50
     * IMPORTANT: If internship has no required skills defined, return 0 (prevents inflated scores for placeholder internships).
     */
    private static int computeRequiredSkillScore(Set<String> required, double weightedMatch, List<String> flags) {
        if (required == null || required.isEmpty()) {
            flags.add("NO_REQUIRED_SKILLS_DEFINED");
            return 0;
        }
        return computeLinearSkillScore(W_REQUIRED, required.size(), weightedMatch);
    }

    /**
     * Preferred skills: 0..20
     * If none defined => 0.
     */
    private static int computePreferredSkillScore(Set<String> preferred, double weightedMatch) {
        if (preferred == null || preferred.isEmpty()) return 0;
        return computeLinearSkillScore(W_PREFERRED, preferred.size(), weightedMatch);
    }

    /**
     * Linear scaling: round(maxPoints * (weightedMatch / totalSkills)), clamped [0..maxPoints]
     */
    private static int computeLinearSkillScore(int maxPoints, int totalSkills, double weightedMatch) {
        if (totalSkills <= 0) return 0;
        double ratio = weightedMatch / (double) totalSkills;
        ratio = Math.min(1.0, Math.max(0.0, ratio));
        return (int) Math.round(maxPoints * ratio);
    }

    private static int computeLevelScore(Enum<?> minimum, Enum<?> studentLevel, int maxPoints) {
        // tuned neutral: if no minimum, do NOT penalize
        if (minimum == null) return maxPoints;
        if (studentLevel == null) return 0;

        int cmp = compareEnums(studentLevel, minimum);
        if (cmp < 0) return 0;        // below min
        if (cmp == 0) return 8;       // meets exactly
        return maxPoints;             // exceeds
    }

    private static int computeExtrasScore(Student s) {
        int score = 0;

        if (s.getGpa() != null) score += 3;
        if (isNotBlank(s.getCertifications())) score += 4;
        if (isNotBlank(s.getLanguages())) score += 3;

        // clamp to W_EXTRAS (avoids “min meaningless” warnings in some IDE setups)
        if (score > W_EXTRAS) score = W_EXTRAS;
        if (score < 0) score = 0;
        return score;
    }

    // ---------------- utility helpers ----------------

    private static boolean isBelow(Enum<?> studentLevel, Enum<?> minimum) {
        if (minimum == null) return false;
        if (studentLevel == null) return true;
        return compareEnums(studentLevel, minimum) < 0;
    }

    private static int compareEnums(Enum<?> a, Enum<?> b) {
        // Works if your enums are ordered lowest->highest
        return Integer.compare(a.ordinal(), b.ordinal());
    }

    private static Integer minCap(Integer existing, int newCap) {
        if (existing == null) return newCap;
        return Math.min(existing, newCap);
    }

    private static int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    private static boolean isNotBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private static Set<String> parseCsv(String csv) {
        if (!isNotBlank(csv)) return new HashSet<>();
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(x -> !x.isEmpty())
                .map(String::toLowerCase)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Weighted skill matching:
     * - match in core => 1.0
     * - match in other => 0.6
     */
    private static WeightedMatch weightedMatch(Set<String> target, Set<String> core, Set<String> other) {
        if (target == null || target.isEmpty()) {
            return new WeightedMatch(0.0, List.of());
        }

        List<String> matched = new ArrayList<>();
        double w = 0.0;

        for (String t : target) {
            if (core.contains(t)) {
                matched.add(t);
                w += 1.0;
            } else if (other.contains(t)) {
                matched.add(t);
                w += 0.6;
            }
        }

        matched = matched.stream().distinct().sorted().toList();
        return new WeightedMatch(w, matched);
    }

    private record WeightedMatch(double weightedCount, List<String> matched) {}
}
