
// SHOW / HIDE PASSWORD
document.querySelectorAll(".toggle-password").forEach(button => {
    button.addEventListener("click", function () {
        const target = document.getElementById(this.dataset.target);

        if (target.type === "password") {
            target.type = "text";
            this.innerText = "Hide";
        } else {
            target.type = "password";
            this.innerText = "Show";
        }
    });
});


// SIGNUP FORM
const signupForm = document.getElementById("signupForm");

if (signupForm) {
    signupForm.addEventListener("submit", async function (e) {
        e.preventDefault();

        const name = document.getElementById("signupName").value.trim();
        const email = document.getElementById("signupEmail").value.trim();
        const password = document.getElementById("signupPassword").value;
        const confirmPassword = document.getElementById("confirmPassword").value;
        const message = document.getElementById("signupMessage");

        if (name === "" || email === "") {
            message.style.color = "red";
            message.innerText = "Please fill all fields!";
            return;
        }

        if (password.length < 8) {
            message.style.color = "red";
            message.innerText = "Password must contain at least 8 characters.";
            return;
        }

        if (password !== confirmPassword) {
            message.style.color = "red";
            message.innerText = "Passwords do not match!";
            return;
        }

        message.style.color = "#956da2";
        message.innerText = "Creating your account... ✨";

        try {
            const response = await fetch("http://localhost:8080/api/signup", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    fullName: name,
                    email: email,
                    password: password
                })
            });

            const result = await response.json();

            message.style.color = response.ok ? "green" : "red";
            message.innerText = result.message;

            if (response.ok) {
                signupForm.reset();

                setTimeout(() => {
                    window.location.href = "login.html";
                }, 1500);
            }

        } catch (error) {
            message.style.color = "red";
            message.innerText = "Unable to connect to MatchMint server.";
            console.error(error);
        }
    });
}


// LOGIN FORM
const loginForm = document.getElementById("loginForm");

if (loginForm) {
    loginForm.addEventListener("submit", async function (e) {
        e.preventDefault();

        const email = document.getElementById("loginEmail").value.trim();
        const password = document.getElementById("loginPassword").value;
        const message = document.getElementById("loginMessage");

        if (email === "" || password === "") {
            message.style.color = "red";
            message.innerText = "Please fill all fields!";
            return;
        }

        message.style.color = "#956da2";
        message.innerText = "Checking your details... ✨";

        try {
            const response = await fetch("http://localhost:8080/api/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    email: email,
                    password: password
                })
            });

            const result = await response.json();

            if (response.ok) {
                message.style.color = "green";
                message.innerText = result.message;

                sessionStorage.setItem(
                    "matchmintUser", result.fullName
                );

                setTimeout(() => {
                    window.location.href = "dashboard.html";
                }, 1000);

            } else {
                message.style.color = "red";
                message.innerText = result.message;
            }

        } catch (error) {
            message.style.color = "red";
            message.innerText = "Unable to connect to MatchMint server.";
            console.error(error);
        }
    });
}


