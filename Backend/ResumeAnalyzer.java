
package Backend;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class ResumeAnalyzer {

    public static String analyzeResume(byte[] pdfData) throws Exception {

        // READ PDF
        try (PDDocument document = Loader.loadPDF(pdfData)) {

            PDFTextStripper stripper = new PDFTextStripper();

            String resumeText = stripper.getText(document);


            // EXTRACT SKILLS
            List<String> skills =
                SkillExtractor.extractSkills(resumeText);


            // CALCULATE RESUME SCORE
            Map<String, Integer> scores =
                ResumeScoreCalculator.calculateScore(
                    resumeText, skills
                );

            int overallScore =
                ResumeScoreCalculator.getOverallScore(scores);


            // GET ALL JOB ROLES
            Set<String> availableRoles =
                JobMatcher.getAvailableRoles();

            List<String> jobRoles =
                new ArrayList<>(availableRoles);


            // SORT JOB ROLES BY MATCH PERCENTAGE
            jobRoles.sort(
                Comparator.comparingInt(
                    (String role) ->
                        JobMatcher.calculateMatch(skills, role)
                ).reversed()
            );


            // CREATE JSON RESPONSE
            StringBuilder json = new StringBuilder();

            json.append("{");

            json.append("\"message\":\"Resume analyzed successfully!\",");
            json.append("\"overallScore\":").append(overallScore).append(",");


            // =====================================
            // DETECTED SKILLS
            // =====================================

            json.append("\"skills\":");

            appendStringArray(json, skills);

            json.append(",");


            // =====================================
            // RESUME SCORE BREAKDOWN
            // =====================================

            json.append("\"scores\":{");

            int count = 0;

            for (Map.Entry<String, Integer> entry : scores.entrySet()) {

                json.append("\"")
                    .append(jsonEscape(entry.getKey()))
                    .append("\":")
                    .append(entry.getValue());

                if (++count < scores.size()) {
                    json.append(",");
                }
            }

            json.append("},");


            // =====================================
            // JOB MATCHES AND SKILL GAPS
            // =====================================

            json.append("\"jobMatches\":[");

            for (int i = 0; i < jobRoles.size(); i++) {

                String role = jobRoles.get(i);


                // CALCULATE MATCH PERCENTAGE
                int matchPercentage =
                    JobMatcher.calculateMatch(skills, role);


                // FIND MATCHED SKILLS
                List<String> matchedSkills =
                    JobMatcher.findMatchedSkills(skills, role);


                // FIND MISSING SKILLS
                List<String> missingSkills =
                    JobMatcher.findMissingSkills(skills, role);


                json.append("{");

                json.append("\"role\":\"")
                    .append(jsonEscape(role))
                    .append("\",");

                json.append("\"matchPercentage\":")
                    .append(matchPercentage)
                    .append(",");

                json.append("\"matchedSkills\":");

                appendStringArray(json, matchedSkills);

                json.append(",");

                json.append("\"missingSkills\":");

                appendStringArray(json, missingSkills);

                json.append("}");


                if (i < jobRoles.size() - 1) {
                    json.append(",");
                }
            }

            json.append("]");


            // CLOSE JSON
            json.append("}");

            return json.toString();

        }
    }


    // =====================================
    // CONVERT STRING LIST INTO JSON ARRAY
    // =====================================

    private static void appendStringArray(
            StringBuilder json,
            List<String> values) {

        json.append("[");

        for (int i = 0; i < values.size(); i++) {

            json.append("\"")
                .append(jsonEscape(values.get(i)))
                .append("\"");

            if (i < values.size() - 1) {
                json.append(",");
            }
        }

        json.append("]");
    }


    // =====================================
    // ESCAPE SPECIAL JSON CHARACTERS
    // =====================================

    private static String jsonEscape(String value) {

        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");

    }

}
