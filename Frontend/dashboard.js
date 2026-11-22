
// =====================================
// MATCHMINT DASHBOARD
// =====================================


// GET LOGGED-IN USER
const loggedInUser = sessionStorage.getItem("matchmintUser");


// CHECK LOGIN
if (!loggedInUser) {
    window.location.href = "login.html";
}


// DISPLAY USER NAME
if (loggedInUser) {

    const dashboardUserName =
        document.getElementById("dashboardUserName");

    const welcomeUserName =
        document.getElementById("welcomeUserName");

    const profileAvatar =
        document.getElementById("profileAvatar");

    if (dashboardUserName) {
        dashboardUserName.innerText = loggedInUser;
    }

    if (welcomeUserName) {
        welcomeUserName.innerText = loggedInUser;
    }

    if (profileAvatar) {
        profileAvatar.innerText =
            loggedInUser.trim().charAt(0).toUpperCase();
    }
}


// =====================================
// LOGOUT
// =====================================

const logoutButton = document.getElementById("logoutButton");

if (logoutButton) {

    logoutButton.addEventListener("click", function () {

        sessionStorage.removeItem("matchmintUser");

        window.location.href = "login.html";

    });

}


// =====================================
// GET DASHBOARD ELEMENTS
// =====================================

const uploadButton = document.getElementById("uploadButton");
const resumeFile = document.getElementById("resumeFile");
const fileMessage = document.getElementById("fileMessage");

const resultsContainer =
    document.getElementById("analysisResults");

const resumeScoreValue =
    document.getElementById("resumeScoreValue");

const jobMatchesValue =
    document.getElementById("jobMatchesValue");

const skillsCountValue =
    document.getElementById("skillsCountValue");

const jobMatchesSection =
    document.getElementById("matches");

const skillGapsSection =
    document.getElementById("skills");


// =====================================
// RESUME UPLOAD
// =====================================

if (uploadButton && resumeFile) {

    uploadButton.addEventListener("click", function () {

        resumeFile.click();

    });


    resumeFile.addEventListener("change", async function () {

        const file = resumeFile.files[0];

        if (!file) {
            return;
        }


        // PDF VALIDATION
        const extension = file.name
            .split(".")
            .pop()
            .toLowerCase();

        if (extension !== "pdf") {

            fileMessage.style.color = "red";

            fileMessage.innerText =
                "Please upload a PDF file. DOC and DOCX support is coming soon.";

            resumeFile.value = "";

            return;
        }


        // FILE SIZE VALIDATION
        if (file.size > 5 * 1024 * 1024) {

            fileMessage.style.color = "red";

            fileMessage.innerText =
                "File is too large! Maximum size is 5 MB.";

            resumeFile.value = "";

            return;
        }


        // UPLOADING MESSAGE
        fileMessage.style.color = "#956da2";

        fileMessage.innerText =
            "Uploading and analyzing your resume... ✨";

        uploadButton.innerText = "Analyzing...";

        uploadButton.disabled = true;


        if (resultsContainer) {
            resultsContainer.innerHTML = "";
            resultsContainer.hidden = true;
        }


        try {

            // CONVERT PDF TO BASE64
            const base64File = await new Promise((resolve, reject) => {

                const reader = new FileReader();

                reader.onload = function () {

                    const base64 = reader.result.split(",")[1];

                    resolve(base64);

                };

                reader.onerror = function () {

                    reject(new Error("Unable to read PDF file."));

                };

                reader.readAsDataURL(file);

            });


            // SEND RESUME TO JAVA BACKEND
            const response = await fetch(
                "http://localhost:8080/api/analyze",
                {
                    method: "POST",

                    headers: {
                        "Content-Type": "application/json"
                    },

                    body: JSON.stringify({
                        file: base64File
                    })
                }
            );


            const result = await response.json();


            if (!response.ok) {

                throw new Error(
                    result.message || "Analysis failed."
                );

            }


            // SUCCESS
            fileMessage.style.color = "green";

            fileMessage.innerText =
                "Resume analyzed successfully! 🎉";


            // DISPLAY EVERYTHING
            displayAnalysisResults(result);


        } catch (error) {

            fileMessage.style.color = "red";

            fileMessage.innerText = error.message;

            console.error("MatchMint Error:", error);

        } finally {

            uploadButton.innerText = "Upload Another Resume ↑";

            uploadButton.disabled = false;

        }

    });

}


// =====================================
// HELPER: CREATE SKILL TAG
// =====================================

function createSkillTag(skill, type) {

    const tag = document.createElement("span");

    tag.innerText = skill;

    tag.style.display = "inline-block";
    tag.style.padding = "8px 13px";
    tag.style.margin = "4px";
    tag.style.borderRadius = "20px";
    tag.style.fontSize = "13px";
    tag.style.fontWeight = "600";

    if (type === "matched") {

        tag.style.background = "#e5f0d9";
        tag.style.color = "#405f2b";

    } else {

        tag.style.background = "#ffe5e2";
        tag.style.color = "#a34743";

    }

    return tag;
}


// =====================================
// HELPER: CREATE SECTION HEADING
// =====================================

function createHeading(text) {

    const heading = document.createElement("h2");

    heading.innerText = text;
    heading.style.color = "#35183f";
    heading.style.marginBottom = "10px";

    return heading;
}


// =====================================
// HELPER: CREATE JOB ROLE CARD
// =====================================

function createJobCard(job, showSkillGaps) {

    const card = document.createElement("div");

    card.style.background = "#fffaf3";
    card.style.border = "1px solid #f0dfed";
    card.style.borderRadius = "18px";
    card.style.padding = "20px";
    card.style.marginBottom = "16px";


    // ROLE NAME
    const roleName = document.createElement("h3");

    roleName.innerText = job.role;
    roleName.style.color = "#35183f";
    roleName.style.margin = "0 0 12px";

    card.appendChild(roleName);


    // MATCH PERCENTAGE
    const percentage = Number(job.matchPercentage) || 0;

    const percentageText = document.createElement("h2");

    percentageText.innerText = percentage + "% Match";
    percentageText.style.color =
        percentage >= 70 ? "#5c8233" :
        percentage >= 40 ? "#c17b35" : "#bd625e";

    percentageText.style.margin = "8px 0";


    card.appendChild(percentageText);


    // PROGRESS BAR
    const progressBackground = document.createElement("div");

    progressBackground.style.width = "100%";
    progressBackground.style.height = "10px";
    progressBackground.style.background = "#eee5ed";
    progressBackground.style.borderRadius = "20px";
    progressBackground.style.overflow = "hidden";


    const progressFill = document.createElement("div");

    progressFill.style.width =
        Math.min(100, Math.max(0, percentage)) + "%";

    progressFill.style.height = "100%";
    progressFill.style.background =
        percentage >= 70 ? "#91b66d" :
        percentage >= 40 ? "#e5ad69" : "#e58d87";

    progressFill.style.borderRadius = "20px";

    progressBackground.appendChild(progressFill);

    card.appendChild(progressBackground);


    // MATCHED SKILLS
    const matchedHeading = document.createElement("h4");

    matchedHeading.innerText = "✓ Skills you have";

    matchedHeading.style.color = "#55783b";
    matchedHeading.style.margin = "18px 0 8px";

    card.appendChild(matchedHeading);


    const matchedContainer = document.createElement("div");

    const matchedSkills = Array.isArray(job.matchedSkills)
        ? job.matchedSkills
        : [];

    if (matchedSkills.length === 0) {

        const message = document.createElement("p");

        message.innerText = "No matching skills detected yet.";
        message.style.color = "#897990";

        matchedContainer.appendChild(message);

    } else {

        matchedSkills.forEach(skill => {
            matchedContainer.appendChild(
                createSkillTag(skill, "matched")
            );
        });

    }

    card.appendChild(matchedContainer);


    // MISSING SKILLS
    if (showSkillGaps) {

        const missingHeading = document.createElement("h4");

        missingHeading.innerText = "＋ Skills to improve";

        missingHeading.style.color = "#a34743";
        missingHeading.style.margin = "18px 0 8px";

        card.appendChild(missingHeading);


        const missingContainer = document.createElement("div");

        const missingSkills = Array.isArray(job.missingSkills)
            ? job.missingSkills
            : [];

        if (missingSkills.length === 0) {

            const message = document.createElement("p");

            message.innerText =
                "Amazing! All listed skills were detected.";

            message.style.color = "#55783b";

            missingContainer.appendChild(message);

        } else {

            missingSkills.forEach(skill => {

                missingContainer.appendChild(
                    createSkillTag(skill, "missing")
                );

            });

        }

        card.appendChild(missingContainer);

    }


    return card;
}


// =====================================
// DISPLAY JOB MATCHES
// =====================================

function displayJobMatches(jobs) {

    if (!jobMatchesSection) {
        console.error("Job matches section not found!");
        return;
    }

    jobMatchesSection.innerHTML = "";

    jobMatchesSection.appendChild(
        createHeading("Your Job Matches 💼")
    );


    const description = document.createElement("p");

    description.innerText =
        "Explore career roles ranked by how closely your resume skills match their requirements.";

    description.style.color = "#897990";

    jobMatchesSection.appendChild(description);


    if (!Array.isArray(jobs) || jobs.length === 0) {

        const emptyMessage = document.createElement("p");

        emptyMessage.innerText =
            "No job-role matches were returned. Please try uploading your resume again.";

        jobMatchesSection.appendChild(emptyMessage);

        return;
    }


    jobs.forEach(job => {

        jobMatchesSection.appendChild(
            createJobCard(job, false)
        );

    });

}


// =====================================
// DISPLAY SKILL GAPS
// =====================================

function displaySkillGaps(jobs) {

    if (!skillGapsSection) {
        console.error("Skill gaps section not found!");
        return;
    }

    skillGapsSection.innerHTML = "";

    skillGapsSection.appendChild(
        createHeading("Your Skill Gaps 🎯")
    );


    const description = document.createElement("p");

    description.innerText =
        "Discover which skills you can learn to improve your match for each career role.";

    description.style.color = "#897990";

    skillGapsSection.appendChild(description);


    if (!Array.isArray(jobs) || jobs.length === 0) {

        const emptyMessage = document.createElement("p");

        emptyMessage.innerText =
            "Upload a resume to discover your skill gaps.";

        skillGapsSection.appendChild(emptyMessage);

        return;
    }


    jobs.forEach(job => {

        skillGapsSection.appendChild(
            createJobCard(job, true)
        );

    });

}


// =====================================
// DISPLAY RESUME ANALYSIS RESULTS
// =====================================

function displayAnalysisResults(result) {

    if (!resultsContainer) {
        console.error("Analysis results section not found!");
        return;
    }


    resultsContainer.innerHTML = "";

    resultsContainer.hidden = false;


    // GET RESULT DATA
    const skills = Array.isArray(result.skills)
        ? result.skills
        : [];

    const scores = result.scores || {};

    const jobs = Array.isArray(result.jobMatches)
        ? result.jobMatches
        : [];


    // =====================================
    // UPDATE DASHBOARD STATISTICS
    // =====================================

    if (resumeScoreValue) {

        resumeScoreValue.innerText =
            result.overallScore ?? "--";

    }

    if (skillsCountValue) {

        skillsCountValue.innerText = skills.length;

    }

    if (jobMatchesValue) {

        // Count roles with at least 50% skill match
        const matchingRoles = jobs.filter(job =>
            Number(job.matchPercentage) >= 50
        ).length;

        jobMatchesValue.innerText = matchingRoles;

    }


    // =====================================
    // REPORT HEADING
    // =====================================

    const reportLabel = document.createElement("p");

    reportLabel.innerText = "YOUR RESUME ANALYSIS";

    reportLabel.style.color = "#956da2";
    reportLabel.style.fontSize = "12px";
    reportLabel.style.fontWeight = "bold";
    reportLabel.style.letterSpacing = "2px";

    resultsContainer.appendChild(reportLabel);


    const heading = document.createElement("h2");

    heading.innerText = "Your MatchMint Resume Report 🍋";

    heading.style.color = "#35183f";

    resultsContainer.appendChild(heading);


    // =====================================
    // OVERALL SCORE
    // =====================================

    const scoreSection = document.createElement("div");

    scoreSection.style.textAlign = "center";
    scoreSection.style.padding = "25px";
    scoreSection.style.margin = "20px 0";
    scoreSection.style.background = "#f4f8e9";
    scoreSection.style.borderRadius = "18px";


    const scoreLabel = document.createElement("p");

    scoreLabel.innerText = "YOUR RESUME SCORE";

    scoreLabel.style.color = "#71805c";
    scoreLabel.style.fontWeight = "bold";


    const score = document.createElement("h1");

    score.innerText = (result.overallScore ?? "--") + " / 100";

    score.style.color = "#5c8233";
    score.style.fontSize = "42px";
    score.style.margin = "10px 0";


    scoreSection.appendChild(scoreLabel);
    scoreSection.appendChild(score);

    resultsContainer.appendChild(scoreSection);


    // =====================================
    // SCORE BREAKDOWN
    // =====================================

    const breakdownHeading = createHeading("Score Breakdown");

    resultsContainer.appendChild(breakdownHeading);


    const breakdownContainer = document.createElement("div");

    breakdownContainer.style.display = "grid";
    breakdownContainer.style.gridTemplateColumns =
        "repeat(auto-fit, minmax(180px, 1fr))";

    breakdownContainer.style.gap = "12px";
    breakdownContainer.style.margin = "15px 0 25px";


    for (const [category, points] of Object.entries(scores)) {

        const scoreCard = document.createElement("div");

        scoreCard.style.background = "#fff8ef";
        scoreCard.style.padding = "16px";
        scoreCard.style.borderRadius = "12px";
        scoreCard.style.border = "1px solid #f0dfed";


        const categoryName = document.createElement("p");

        categoryName.innerText = category;

        categoryName.style.color = "#806b89";
        categoryName.style.margin = "0 0 8px";


        const categoryScore = document.createElement("h3");

        categoryScore.innerText = points + " points";

        categoryScore.style.color = "#35183f";
        categoryScore.style.margin = "0";


        scoreCard.appendChild(categoryName);
        scoreCard.appendChild(categoryScore);

        breakdownContainer.appendChild(scoreCard);

    }

    resultsContainer.appendChild(breakdownContainer);


    // =====================================
    // DETECTED SKILLS
    // =====================================

    const skillsHeading = createHeading("Skills Detected ✨");

    resultsContainer.appendChild(skillsHeading);


    const skillsDescription = document.createElement("p");

    skillsDescription.innerText =
        "Here are the skills MatchMint discovered in your resume.";

    skillsDescription.style.color = "#897990";

    resultsContainer.appendChild(skillsDescription);


    const skillsList = document.createElement("div");

    skillsList.style.display = "flex";
    skillsList.style.flexWrap = "wrap";
    skillsList.style.gap = "10px";
    skillsList.style.marginTop = "15px";


    skills.forEach(skill => {

        skillsList.appendChild(
            createSkillTag(skill, "matched")
        );

    });


    resultsContainer.appendChild(skillsList);


    // =====================================
    // UPDATE JOB MATCHES AND SKILL GAPS
    // =====================================

    displayJobMatches(jobs);

    displaySkillGaps(jobs);


    // =====================================
    // SCROLL TO REPORT
    // =====================================

    resultsContainer.scrollIntoView({

        behavior: "smooth",

        block: "start"

    });

}
