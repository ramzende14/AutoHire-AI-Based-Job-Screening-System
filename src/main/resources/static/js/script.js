let token = "";

// Helper functions for UI
function showStatusMessage(message, type) {
    const msgEl = document.getElementById('statusMessage');
    if (!msgEl) {
        console.error("Status message element not found.");
        return;
    }
    msgEl.innerText = message;
    msgEl.className = 'message ' + type;
    msgEl.style.display = 'block';
}

function hideStatusMessage() {
    const msgEl = document.getElementById('statusMessage');
    if (msgEl) {
        msgEl.style.display = 'none';
    }
}

// ✅ Loader Functions
function showLoader() {
    const loader = document.getElementById('loader');
    if (loader) {
        loader.classList.add('active');
    }
}

function hideLoader() {
    const loader = document.getElementById('loader');
    if (loader) {
        loader.classList.remove('active');
    }
}

// User Dashboard Functions (on user_dashboard.html)
function showProfile() {
    // This function will only be used on user_dashboard.html
    const profileName = document.getElementById('profileName');
    if (profileName) profileName.innerText = document.getElementById('fullName').value || "-";
    // ... add all other profile fields
    // document.getElementById('profileJobTitle').innerText = document.getElementById('jobTitle').value || "-";
    // ...

    const profileModal = document.getElementById('profileModal');
    if (profileModal) profileModal.classList.add('is-visible');
}

function closeProfile() {
    const profileModal = document.getElementById('profileModal');
    if (profileModal) profileModal.classList.remove('is-visible');
}

function logout() {
    alert('Logging out...');
    window.location.href = 'login.html';
}

// ✅ Upload Resume (सुधारित आणि मुख्य फंक्शन)
async function uploadResume() {
    hideStatusMessage();
    showLoader(); // Show loader at the beginning of the process

    const requiredFields = ['fullName', 'email', 'phone', 'linkedin', 'github', 'location', 'education', 'experience', 'skills', 'jobTitle', 'jobId'];
    for (let id of requiredFields) {
        const el = document.getElementById(id);
        if (!el || !el.value) { // Added check for element existence
            hideLoader();
            showStatusMessage("Please fill all required fields! " + (el ? el.previousElementSibling.innerText : id), "error");
            el && el.focus();
            return;
        }
    }

    const resumeFile = document.getElementById('resumeFile')?.files[0];
    if (!resumeFile) {
        hideLoader();
        showStatusMessage("Please upload your resume!", "error");
        return;
    }

    const formData = new FormData();
    formData.append("file", resumeFile);

    const jobIdValue = document.getElementById('jobId')?.value;
    if (jobIdValue === undefined || isNaN(Number(jobIdValue))) {
        hideLoader();
        showStatusMessage("Job ID must be a valid number.", "error");
        return;
    }
    formData.append("jobId", Number(jobIdValue));

    const token = localStorage.getItem("token");
    if (!token) {
        hideLoader();
        showStatusMessage("Please log in to submit your application.", "error");
        return;
    }

    try {
        const res = await fetch("/api/resume/upload", {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + token
            },
            body: formData
        });

        const data = await res.json();

        if (res.ok) {
            showStatusMessage("Application submitted successfully! Score: " + data.score, "success");
            console.log("Backend response:", data);
        } else {
            showStatusMessage("Application submission failed: " + data.error, "error");
            console.error("Backend error:", data.error);
        }

    } catch (error) {
        showStatusMessage("An error occurred. Please check your network connection.", "error");
        console.error("Fetch API error:", error);
    } finally {
        hideLoader();
    }
}

// ✅ Login
async function login() {
    hideStatusMessage();
    showLoader(); // Show loader on login attempt

    const email = document.getElementById('loginEmail')?.value;
    const password = document.getElementById('loginPassword')?.value;

    if (!email || !password) {
        hideLoader();
        showStatusMessage("Please enter email and password.", "error");
        return;
    }

    try {
        const res = await fetch("/api/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, password })
        });

        const data = await res.json();

        if (res.ok) {
            localStorage.setItem("token", data.token);
            showStatusMessage("Login successful! Redirecting...", "success");

            setTimeout(() => {
                window.location.href = "user_dashboard.html";
            }, 1000);
        } else {
            showStatusMessage(data.error || "Login failed.", "error");
        }
    } catch (error) {
        showStatusMessage("Network error occurred.", "error");
        console.error(error);
    } finally {
        hideLoader(); // Hide loader after the request is finished
    }
}
async function register() {
    hideStatusMessage();

    const name = document.getElementById('regName')?.value.trim();
    const email = document.getElementById('regEmail')?.value.trim();
    const password = document.getElementById('regPassword')?.value.trim();
    const role = document.getElementById('regRole')?.value || "USER";

    if (!name || !email || !password) {
        showStatusMessage("Please fill all fields!", "error");
        return;
    }

    try {
        const res = await fetch("/api/auth/register", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name, email, password, role })
        });

        const data = await res.text(); // ✅ Important: backend returns plain text

        if (res.ok) {
            showStatusMessage(data || "Registration successful! You can now log in.", "success");
        } else {
            showStatusMessage(data || "Registration failed", "error");
        }
    } catch (err) {
        showStatusMessage("Network error", "error");
        console.error(err);
    }
}

