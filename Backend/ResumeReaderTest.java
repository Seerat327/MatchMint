
package Backend;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class ResumeReaderTest {

    public static void main(String[] args) {

        String filePath = "C:\\Users\\Seerat\\Downloads\\MatchMint_Sample_Resume_Ananya.pdf";

        try (PDDocument document = Loader.loadPDF(new File(filePath))) {

            PDFTextStripper stripper = new PDFTextStripper();
            String resumeText = stripper.getText(document);

            System.out.println("===== MATCHMINT RESUME ANALYZER =====");

            // STEP 1: EXTRACT SKILLS
            List<String> skills =
                SkillExtractor.extractSkills(resumeText);

            System.out.println("\n===== SKILLS DETECTED =====");

            for (String skill : skills) {
                System.out.println("✓ " + skill);
            }

            // STEP 2: CALCULATE SCORE
            Map<String, Integer> scores =
                ResumeScoreCalculator.calculateScore(resumeText, skills);

            System.out.println("\n===== RESUME SCORE BREAKDOWN =====");

            for (Map.Entry<String, Integer> entry : scores.entrySet()) {
                System.out.println(
                    entry.getKey() + ": " + entry.getValue()
                );
            }

            // STEP 3: OVERALL SCORE
            int overallScore =
                ResumeScoreCalculator.getOverallScore(scores);

            System.out.println("\n==============================");
            System.out.println("YOUR MATCHMINT RESUME SCORE");
            System.out.println("==============================");

            System.out.println(overallScore + " / 100");

            System.out.println("\n===== ANALYSIS COMPLETED =====");

        } catch (Exception e) {

            System.out.println("Unable to analyze resume.");
            e.printStackTrace();

        }
    }
}



