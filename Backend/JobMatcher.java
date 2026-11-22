
package Backend;

import java.util.*;

public class JobMatcher {

    private static final Map<String, List<String>> jobSkills =
            new LinkedHashMap<>();

    static {

        jobSkills.put("Frontend Developer", Arrays.asList(
            "HTML", "CSS", "JavaScript", "React",
            "Git", "GitHub", "TypeScript",
            "Responsive Design", "REST API"
        ));

        jobSkills.put("Backend Developer", Arrays.asList(
            "Java", "Python", "SQL", "MySQL",
            "Node.js", "REST API", "OOP",
            "Git", "Data Structures"
        ));

        jobSkills.put("Full Stack Developer", Arrays.asList(
            "HTML", "CSS", "JavaScript", "React",
            "Node.js", "MongoDB", "SQL",
            "REST API", "Git"
        ));

        jobSkills.put("Python Developer", Arrays.asList(
            "Python", "SQL", "OOP", "Data Structures",
            "MySQL", "Git", "REST API"
        ));

        jobSkills.put("Java Developer", Arrays.asList(
            "Java", "OOP", "SQL", "JDBC",
            "Data Structures", "MySQL", "Git"
        ));

        jobSkills.put("AI/ML Engineer", Arrays.asList(
            "Python", "Machine Learning",
            "Artificial Intelligence", "NumPy",
            "Pandas", "Data Analysis", "SQL"
        ));

        jobSkills.put("Data Analyst", Arrays.asList(
            "Python", "SQL", "Data Analysis",
            "Pandas", "NumPy", "Excel",
            "Data Visualization"
        ));

        jobSkills.put("Data Scientist", Arrays.asList(
            "Python", "SQL", "Machine Learning",
            "Pandas", "NumPy", "Statistics",
            "Data Analysis", "Data Visualization"
        ));

        jobSkills.put("Software Engineer", Arrays.asList(
            "Java", "Python", "C", "OOP",
            "Data Structures", "SQL", "Git",
            "Problem Solving"
        ));

        jobSkills.put("UI/UX Designer", Arrays.asList(
            "Figma", "UI Design", "UX Design",
            "Wireframing", "Prototyping",
            "User Research", "Responsive Design"
        ));
    }


    // GET ALL JOB ROLES
    public static Set<String> getAvailableRoles() {
        return jobSkills.keySet();
    }


    // GET REQUIRED SKILLS
    public static List<String> getRequiredSkills(String jobRole) {

        return jobSkills.getOrDefault(
            jobRole,
            Collections.emptyList()
        );

    }


    // CALCULATE MATCH PERCENTAGE
    public static int calculateMatch(
            List<String> resumeSkills,
            String jobRole) {

        List<String> requiredSkills = getRequiredSkills(jobRole);

        if (requiredSkills.isEmpty()) {
            return 0;
        }

        int matchedSkills = 0;

        for (String skill : requiredSkills) {

            if (containsSkill(resumeSkills, skill)) {
                matchedSkills++;
            }

        }

        return (matchedSkills * 100) / requiredSkills.size();

    }


    // FIND MATCHED SKILLS
    public static List<String> findMatchedSkills(
            List<String> resumeSkills,
            String jobRole) {

        List<String> matched = new ArrayList<>();

        for (String skill : getRequiredSkills(jobRole)) {

            if (containsSkill(resumeSkills, skill)) {
                matched.add(skill);
            }

        }

        return matched;

    }


    // FIND MISSING SKILLS
    public static List<String> findMissingSkills(
            List<String> resumeSkills,
            String jobRole) {

        List<String> missing = new ArrayList<>();

        for (String skill : getRequiredSkills(jobRole)) {

            if (!containsSkill(resumeSkills, skill)) {
                missing.add(skill);
            }

        }

        return missing;

    }


    // CHECK WHETHER RESUME CONTAINS A SKILL
    private static boolean containsSkill(
            List<String> resumeSkills,
            String requiredSkill) {

        for (String skill : resumeSkills) {

            if (skill.equalsIgnoreCase(requiredSkill)) {
                return true;
            }

        }

        return false;

    }

}
