/* =============================
   LOGIN
============================= */
const BASE_URL = "https://pims-backend-xa8s.onrender.com";
async function login() {
  const email = document.getElementById("loginEmail").value;
  const password = document.getElementById("loginPassword").value;
  const errorEl = document.getElementById("loginError");

  const loginBtn = document.querySelector("#loginSection button");

  errorEl.innerText = "";

  // 🔥 Add loading state
  loginBtn.innerText = "Logging in...";
  loginBtn.disabled = true;

  try {
    const response = await fetch(`${BASE_URL}/api/auth/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ email, password }),
    });

    const data = await response.json();

    if (!response.ok) {
      errorEl.innerText = data.error || "Login failed.";
      loginBtn.innerText = "Login";
      loginBtn.disabled = false;
      return;
    }

    localStorage.setItem("token", data.token);
    localStorage.setItem("role", data.role);

    if (data.role === "STUDENT") {
      window.location.href = "student.html";
    } else if (data.role === "ADMIN") {
      window.location.href = "admin.html";
    } else if (data.role === "COMPANY") {
      window.location.href = "company.html";
    }

  } catch (error) {
    errorEl.innerText = "Server is waking up... please wait.";
    loginBtn.innerText = "Login";
    loginBtn.disabled = false;
  }
}

/* =============================
   REGISTER
============================= */

async function register() {
  const email = document.getElementById("registerEmail").value;
  const password = document.getElementById("registerPassword").value;
  const role = document.getElementById("registerRole").value;
  const errorEl = document.getElementById("registerError");

  const registerBtn = document.querySelector("#registerSection button");

  errorEl.innerText = "";

  // 🔥 Loading state
  registerBtn.innerText = "Processing...";
  registerBtn.disabled = true;

  try {
    const response = await fetch(`${BASE_URL}/api/auth/register`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ email, password, role }),
    });

    const data = await response.json().catch(() => ({}));

    if (!response.ok) {
      errorEl.innerText = data.error || "Registration failed.";
      registerBtn.innerText = "Register";
      registerBtn.disabled = false;
      return;
    }

    alert("Registration successful! Please login.");
    showLogin();

  } catch (error) {
    errorEl.innerText = "Server is waking up... please wait.";
  }

  registerBtn.innerText = "Register";
  registerBtn.disabled = false;
}

/* =============================
   TOGGLE SECTIONS
============================= */

function showRegister() {
  document.getElementById("loginSection").classList.add("hidden");
  document.getElementById("registerSection").classList.remove("hidden");
}

function showLogin() {
  document.getElementById("registerSection").classList.add("hidden");
  document.getElementById("loginSection").classList.remove("hidden");
}

function togglePassword(inputId, icon) {
  const input = document.getElementById(inputId);
  const svg = icon.querySelector("svg");
  const slash = svg.querySelector(".eye-slash");

  if (input.type === "password") {
    input.type = "text";
    slash.style.display = "block";
  } else {
    input.type = "password";
    slash.style.display = "none";
  }
}
