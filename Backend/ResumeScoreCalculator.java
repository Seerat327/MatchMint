
package Backend;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResumeScoreCalculator {

    public static Map<String, Integer> calculateScore(
            String resumeText, List<String> skills) {

        Map<String, Integer> scores = new LinkedHashMap<>();

        String text = resumeText.toLowerCase();

        // 1. SKILLS SCORE (30 points)
        int skillsScore = Math.min(skills.size() * 3, 30);
        scores.put("Skills", skillsScore);

        // 2. PROJECTS SCORE (25 points)
        int projectsScore = 0;

        if (text.contains("projects")) {
            projectsScore = 15;

            if (text.contains("student management")
                    || text.contains("portfolio")) {
                projectsScore = 25;
            }
        }

        scores.put("Projects", projectsScore);

        // 3. EDUCATION SCORE (20 points)
        int educationScore = 0;

        if (text.contains("education")
                || text.contains("b.tech")
                || text.contains("bachelor")) {
            educationScore = 20;
        }

        scores.put("Education", educationScore);

        // 4. EXPERIENCE SCORE (10 points)
        int experienceScore = 0;

        if (text.contains("internship")
                || text.contains("intern")
                || text.contains("professional experience")) {
            experienceScore = 10;
        } else if (text.contains("fresher")
                && projectsScore > 0) {
            experienceScore = 8;
        } else if (text.contains("experience")) {
            experienceScore = 5;
        }

        scores.put("Experience", experienceScore);

        // 5. RESUME COMPLETENESS (15 points)
        int completenessScore = 0;

        if (text.contains("@")) completenessScore += 3;
        if (text.contains("+91")) completenessScore += 3;
        if (text.contains("summary")) completenessScore += 3;
        if (text.contains("skills")) completenessScore += 3;
        if (text.contains("career interests")) completenessScore += 3;

        scores.put("Resume Completeness", completenessScore);

        return scores;
    }

    public static int getOverallScore(Map<String, Integer> scores) {

        int total = 0;

        for (int score : scores.values()) {
            total += score;
        }

        return total;
    }
}
