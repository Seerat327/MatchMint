
const resumeInput = document.getElementById("resume");

const chooseFile = document.getElementById("chooseFile");

const fileName = document.getElementById("fileName");

const analyzeButton = document.getElementById("analyzeButton");

const dropArea = document.getElementById("dropArea");

let selectedResume = null;


// OPEN FILE SELECTOR

chooseFile.addEventListener("click", function () {

    resumeInput.click();

});


// VALIDATE AND DISPLAY FILE

function handleFile(file) {

    if (!file) {
        return;
    }

    let allowedTypes = [
        "pdf",
        "doc",
        "docx"
    ];

    let extension = file.name.split(".").pop().toLowerCase();

    if (!allowedTypes.includes(extension)) {

        alert("Please upload a PDF, DOC or DOCX file.");

        resumeInput.value = "";
        selectedResume = null;

        fileName.innerText = "No file selected yet";

        analyzeButton.disabled = true;

        return;
    }

    // File size limit: 5 MB

    if (file.size > 5 * 1024 * 1024) {

        alert("File size should be less than 5 MB.");

        resumeInput.value = "";
        selectedResume = null;

        fileName.innerText = "No file selected yet";

        analyzeButton.disabled = true;

        return;
    }

    selectedResume = file;

    fileName.innerText = "✓ " + file.name;

    analyzeButton.disabled = false;

}


// FILE INPUT CHANGE

resumeInput.addEventListener("change", function () {

    handleFile(this.files[0]);

});


// DRAG AND DROP

dropArea.addEventListener("dragover", function (event) {

    event.preventDefault();

    dropArea.classList.add("dragging");

});


dropArea.addEventListener("dragleave", function () {

    dropArea.classList.remove("dragging");

});


dropArea.addEventListener("drop", function (event) {

    event.preventDefault();

    dropArea.classList.remove("dragging");

    const file = event.dataTransfer.files[0];

    handleFile(file);

});


// ANALYZE BUTTON

analyzeButton.addEventListener("click", function () {

    if (selectedResume === null) {

        alert("Please select your resume first!");

        return;

    }

    alert(
        "Resume selected successfully!\n\n" +
        "File: " + selectedResume.name +
        "\n\nThe Java resume analysis engine will be connected in the next development stage."
    );

});
