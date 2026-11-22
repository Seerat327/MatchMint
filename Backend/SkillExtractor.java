
package Backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class SkillExtractor {

    public static List<String> extractSkills(String resumeText) {

        List<String> skills = Arrays.asList(
            "Python",
            "Java",
            "C",
            "C++",
            "SQL",
            "MySQL",
            "HTML",
            "CSS",
            "JavaScript",
            "TypeScript",
            "React",
            "Node.js",
            "MongoDB",
            "JDBC",
            "Git",
            "GitHub",
            "Machine Learning",
            "Artificial Intelligence",
            "Data Analysis",
            "Pandas",
            "NumPy",
            "Data Structures",
            "OOP",
            "REST API",
            "Responsive Design",
            "Excel",
            "Data Visualization",
            "Statistics",
            "Problem Solving",
            "Figma",
            "UI Design",
            "UX Design",
            "Wireframing",
            "Prototyping",
            "User Research"
        );

        List<String> foundSkills = new ArrayList<>();

        if (resumeText == null || resumeText.isEmpty()) {
            return foundSkills;
        }

        String text = resumeText.toLowerCase();

        for (String skill : skills) {

            String normalizedSkill = skill.toLowerCase();

            String regex = "(?<![a-z0-9])"
                    + Pattern.quote(normalizedSkill)
                    + "(?![a-z0-9])";

            if (Pattern.compile(regex).matcher(text).find()) {
                foundSkills.add(skill);
            }
        }

        return foundSkills;
    }
}
