/* =============================
   LOGIN
============================= */
const BASE_URL = "https://pims-backend-xa8s.onrender.com";
async function login() {
  const email = document.getElementById("loginEmail").value;
  const password = document.getElementById("loginPassword").value;
  const errorEl = document.getElementById("loginError");

  errorEl.innerText = "";

  try {
    const response = await fetch(`${BASE_URL}/api/auth/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ email, password }),
    });

    const data = await response.json(); // 🔥 always read response

    if (!response.ok) {
      errorEl.innerText = data.error; // 🔥 show backend message
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
    errorEl.innerText = "Something went wrong. Try again.";
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

  errorEl.innerText = "";

  try {
    const response = await fetch(`${BASE_URL}/api/auth/register`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ email, password, role }),
    });

    if (!response.ok) {
      throw new Error("Registration failed");
    }

    alert("Registration successful! Please login.");
    showLogin();
  } catch (error) {
    errorEl.innerText = "Registration failed. Try again.";
  }
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

  if (input.type === "password") {
    input.type = "text";
    icon.textContent = "🙈";
  } else {
    input.type = "password";
    icon.textContent = "👁️";
  }
}
