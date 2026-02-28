/* ===========================
   GLOBAL SETUP
=========================== */

const token = localStorage.getItem("token");

if (!token) {
  window.location.href = "index.html";
}

//added this for profile completion flow, can be removed later
window.onload = async function () {
  await checkProfileStatus();
  handleRoute();
};


let cachedJobs = [];
let currentRoute = null;
let pendingMessage = null;

async function checkProfileStatus() {
  try {
    const res = await fetch(
      "http://localhost:8080/api/student/profile",
      {
        headers: { Authorization: "Bearer " + token }
      }
    );

    if (!res.ok) return;

    const data = await res.json();

    // 🔥 If branch is null or fullName is null → profile incomplete
    if (!data.branch || !data.fullName) {
      window.location.href = "complete-student-profile.html";
    }

  } catch (err) {
    console.log("Profile check failed");
  }
}

/* ===========================
   ROUTING
=========================== */

function navigate(section) {
  if (currentRoute === section) return;
  window.location.hash = section;
}


function handleRoute() {
  const section = window.location.hash.replace("#", "") || "dashboard";

  if (currentRoute === section) return;
  currentRoute = section;

  if (section === "dashboard") loadDashboard();
  else if (section === "jobs") loadJobs();
  else if (section === "applications") loadApplications();
  else if (section === "profile") loadProfile();
}

window.addEventListener("hashchange", handleRoute);

/* ===========================
   UTILITIES
=========================== */

function logout() {
  localStorage.removeItem("token");
  window.location.href = "index.html";
}

function showLoader() {
  document.getElementById("loader").classList.remove("hidden");
}

function hideLoader() {
  document.getElementById("loader").classList.add("hidden");
}

function showMessage(text, type) {
  alert(text);
}

// function showMessage(text, type) {
//   const box = document.getElementById("messageBox");
//   box.textContent = text;
//   box.className = "message " + type;
//   box.classList.remove("hidden");

//   setTimeout(() => box.classList.add("hidden"), 3000);
// }

function clearMainSections() {
  document.getElementById("content").innerHTML = "";
  document.getElementById("profile-section").classList.add("hidden");
  document.getElementById("jobControls").classList.add("hidden");
}

/* ===========================
   DASHBOARD
=========================== */

async function loadDashboard() {
  clearMainSections();
  showLoader();

  try {
    const [appsRes, jobsRes] = await Promise.all([
      fetch("http://localhost:8080/api/student/applications", {
        headers: { Authorization: "Bearer " + token }
      }),
      fetch("http://localhost:8080/api/student/jobs", {
        headers: { Authorization: "Bearer " + token }
      })
    ]);

    const applications = await appsRes.json();
    const jobs = await jobsRes.json();

    const shortlisted = applications.filter(a => a.status === "SHORTLISTED").length;
    const rejected = applications.filter(a => a.status === "REJECTED").length;

    document.getElementById("content").innerHTML = `
      <h2>Dashboard Overview</h2>
      <div class="stats-container">
        <div class="stat-card">
          <h3>${applications.length}</h3>
          <p>Total Applications</p>
        </div>
        <div class="stat-card">
          <h3>${shortlisted}</h3>
          <p>Shortlisted</p>
        </div>
        <div class="stat-card">
          <h3>${rejected}</h3>
          <p>Rejected</p>
        </div>
        <div class="stat-card">
          <h3>${jobs.length}</h3>
          <p>Available Jobs</p>
        </div>
      </div>
    `;
  } catch {
    showMessage("Failed to load dashboard.", "error");
  }

  hideLoader();
}

/* ===========================
   JOBS
=========================== */

async function loadJobs() {
  clearMainSections();
  showLoader();

  document.getElementById("jobControls").classList.remove("hidden");

  try {
    const res = await fetch("http://localhost:8080/api/student/jobs", {
      headers: { Authorization: "Bearer " + token }
    });

    cachedJobs = await res.json();
    renderJobs(cachedJobs);
  } catch {
    showMessage("Failed to load jobs.", "error");
  }

  hideLoader();
}

function renderJobs(jobs) {
  const content = document.getElementById("content");
  content.innerHTML = `<h2>Available Jobs</h2>`;

  if (!jobs.length) {
    content.innerHTML += "<p>No jobs found.</p>";
    return;
  }

  jobs.forEach(job => {
    content.innerHTML += `
      <div class="card">
        <h4>${job.title}</h4>
        <p>${job.companyName}</p>
        <p>CTC: ${job.ctcOffered} LPA</p>
        <p>Deadline: ${job.deadline}</p>

        <div class="job-actions">
          <button class="btn-outline" onclick="viewJob(${job.id})">
            View Details
          </button>

          ${
            job.alreadyApplied
              ? `<button class="btn-disabled" disabled>Applied</button>`
              : `<button class="btn-primary" onclick="applyJob(${job.id})">Apply</button>`
          }
        </div>
      </div>
    `;
  });
}

function viewJob(id) {
  const job = cachedJobs.find(j => j.id === id);
  if (!job) return;

  const modal = document.getElementById("jobModal");
  const body = document.getElementById("modalBody");

  body.innerHTML = `
    <h3>${job.title}</h3>
    <p><strong>Company:</strong> ${job.companyName}</p>
    <p><strong>Location:</strong> ${job.location}</p>
    <p><strong>CTC:</strong> ${job.ctcOffered} LPA</p>
    <p><strong>Minimum CGPA:</strong> ${job.minCgpa}</p>
    <p><strong>Deadline:</strong> ${job.deadline}</p>
    <p>${job.description}</p>
  `;

  modal.classList.add("active");
}

function closeModal() {
  document.getElementById("jobModal").classList.remove("active");
}

async function applyJob(jobId) {
  try {
    const res = await fetch(
      `http://localhost:8080/api/student/apply/${jobId}`,
      {
        method: "POST",
        headers: { Authorization: "Bearer " + token }
      }
    );

    if (!res.ok) throw new Error();

    showMessage("Applied successfully!", "success");
    loadJobs();
  } catch {
    showMessage("Application failed.", "error");
  }
}

/* ===========================
   FILTERS
=========================== */

function applyFilters() {
  const search = document.getElementById("searchInput").value.toLowerCase();
  const branch = document.getElementById("branchFilter").value;
  const cgpa = document.getElementById("cgpaFilter").value;

  const filtered = cachedJobs.filter(job => {
    const matchesSearch = job.title.toLowerCase().includes(search);
    const matchesBranch = branch ? job.eligibleBranches?.includes(branch) : true;
    const matchesCgpa = cgpa ? job.minimumCgpa <= parseFloat(cgpa) : true;

    return matchesSearch && matchesBranch && matchesCgpa;
  });

  renderJobs(filtered);
}

function clearFilters() {
  document.getElementById("searchInput").value = "";
  document.getElementById("branchFilter").value = "";
  document.getElementById("cgpaFilter").value = "";
  renderJobs(cachedJobs);
}

/* ===========================
   APPLICATIONS
=========================== */

async function loadApplications() {
  clearMainSections();
  showLoader();

  try {
    const res = await fetch(
      "http://localhost:8080/api/student/applications",
      { headers: { Authorization: "Bearer " + token } }
    );

    const applications = await res.json();

    const content = document.getElementById("content");
    content.innerHTML = `<h2>My Applications</h2>`;

    applications.forEach(app => {
      content.innerHTML += `
        <div class="card">
          <p><strong> ${app.jobTitle}</strong></p>
          <p>Company : ${app.companyName}</p>
          <p>Applied On :${new Date(app.appliedDate).toLocaleDateString()}</p>
          <p>Status: <span class="status ${app.status}">${app.status}</span></p>
        </div>
      `;
    });
  } catch {
    showMessage("Failed to load applications.", "error");
  }

  hideLoader();
}
/* ===========================
   PROFILE
=========================== */

let currentProfileData = null;

async function loadProfile() {
  clearMainSections();
  showLoader();

  try {
    const res = await fetch(
      "http://localhost:8080/api/student/profile",
      { headers: { Authorization: "Bearer " + token } }
    );

    if (!res.ok) throw new Error();

    const data = await res.json();
    currentProfileData = data;

    const section = document.getElementById("profile-section");
    section.classList.remove("hidden");

    section.innerHTML = `
      <div class="profile-header">
        <h2>My Profile</h2>
        <button class="btn-outline" onclick="openEditProfile()">
          Edit Profile
        </button>
      </div>

      <div class="profile-card">
        <h3>Basic Information</h3>
        <p><strong>Name:</strong> ${data.fullName}</p>
        <p><strong>Email:</strong> ${data.email}</p>
        <p><strong>USN:</strong> ${data.usn || "Not added"}</p>
        <p><strong>Branch:</strong> ${data.branch}</p>
        <p><strong>CGPA:</strong> ${data.cgpa || "Not added"}</p>
        <p><strong>Graduation Year:</strong> ${data.graduationYear || "Not added"}</p>
        <p><strong>Location:</strong> ${data.location || "Not added"}</p>
      </div>

      <div class="profile-card">
        <h3>Career Objective</h3>
        <p>${data.careerObjective || "Not added yet"}</p>
      </div>

      <div class="profile-card">
        <h3>Skills</h3>
        <div class="skills-container">
          ${
            data.skills
              ? data.skills.split(",").map(skill =>
                  `<span class="skill-badge">${skill.trim()}</span>`
                ).join("")
              : "Not added yet"
          }
        </div>
      </div>

      <div class="profile-card">
        <h3>Resume</h3>

        <div class="resume-actions-row">
          ${
            data.resumeUrl
              ? `
                <a class="btn-primary"
                   href="http://localhost:8080/${data.resumeUrl}"
                   target="_blank">
                   🔍 View Resume
                </a>

                <button class="btn-outline danger"
                        onclick="deleteResume()">
                    🗑 Delete
                </button>
              `
              : `<span class="no-resume-text">No resume uploaded</span>`
          }

          <button class="btn-outline"
                  onclick="triggerResumeUpload()">
              📤 Upload Resume
          </button>
        </div>

        <input type="file"
               id="resumeInput"
               accept="application/pdf"
               style="display:none"
               onchange="uploadResume()" />
      </div>
    `;

  } catch {
    showMessage("Failed to load profile.", "error");
  }

  hideLoader();

if (pendingMessage) {
  showMessage(pendingMessage.text, pendingMessage.type);
  pendingMessage = null;
}

}

/* ===========================
   EDIT PROFILE
=========================== */

function openEditProfile() {
  const modal = document.getElementById("editProfileModal");
  modal.classList.add("active");

  const data = currentProfileData;
  document.getElementById("editUsn").value = data.usn || "";
  document.getElementById("editFullName").value = data.fullName || "";
  document.getElementById("editBranch").value = data.branch || "";
  document.getElementById("editCgpa").value = data.cgpa || "";
  document.getElementById("editGradYear").value = data.graduationYear || "";
  document.getElementById("editLocation").value = data.location || "";
  document.getElementById("editObjective").value = data.careerObjective || "";
  document.getElementById("editSkills").value = data.skills || "";
}

function closeEditModal() {
  document.getElementById("editProfileModal").classList.remove("active");
}

document.getElementById("editProfileForm").addEventListener("submit", async function (e) {
  e.preventDefault();

  const updatedData = {
    usn: document.getElementById("editUsn").value,
    fullName: document.getElementById("editFullName").value,
    branch: document.getElementById("editBranch").value,
    cgpa: document.getElementById("editCgpa").value,
    graduationYear: document.getElementById("editGradYear").value,
    location: document.getElementById("editLocation").value,
    careerObjective: document.getElementById("editObjective").value,
    skills: document.getElementById("editSkills").value
  };

  try {
    const res = await fetch(
      "http://localhost:8080/api/student/profile",
      {
        method: "PUT",
        headers: {
          Authorization: "Bearer " + token,
          "Content-Type": "application/json"
        },
        body: JSON.stringify(updatedData)
      }
    );

    if (!res.ok) throw new Error();

    showMessage("Profile updated successfully!", "success");
    closeEditModal();
    loadProfile();

  } catch {
    showMessage("Failed to update profile.", "error");
  }
});


/* ===========================
   RESUME
=========================== */

function triggerResumeUpload() {
  document.getElementById("resumeInput").click();
}

// async function uploadResume() {
//   const file = document.getElementById("resumeInput").files[0];
//   if (!file) return;

//   const formData = new FormData();
//   formData.append("file", file);

//   try {
//     const res = await fetch(
//       "http://localhost:8080/api/student/upload-resume",
//       {
//         method: "POST",
//         headers: { Authorization: "Bearer " + token },
//         body: formData
//       }
//     );

//     if (!res.ok) throw new Error();

//     showMessage("Resume uploaded!", "success");
//     loadProfile();
//   } catch {
//     showMessage("Upload failed.", "error");
//   }
// }
async function uploadResume() {
  const file = document.getElementById("resumeInput").files[0];
  if (!file) return;

  const formData = new FormData();
  formData.append("file", file);


  try {
    const res = await fetch(
      "http://localhost:8080/api/student/upload-resume",
      {
        method: "POST",
        headers: { Authorization: "Bearer " + token },
        body: formData
      }
    );

    if (!res.ok) throw new Error();
      showMessage("Resume uploaded successfully!", "success");
      loadProfile(); // Let routing reload once only

  } catch {
    showMessage("Failed to upload resume.", "error");
  }
}

async function deleteResume() {
  try {
    const res = await fetch(
      "http://localhost:8080/api/student/delete-resume",
      {
        method: "DELETE",
        headers: { Authorization: "Bearer " + token }
      }
    );

    if (!res.ok) throw new Error();

      showMessage("Resume deleted successfully!", "success");
      loadProfile();// Let routing reload once only
  } catch {
    showMessage("Failed to delete resume.", "error");
  }
}
