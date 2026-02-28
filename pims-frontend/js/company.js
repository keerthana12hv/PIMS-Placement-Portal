const token = localStorage.getItem("token");

if (!token) {
  window.location.href = "index.html";
}

function logout() {
  localStorage.removeItem("token");
  window.location.href = "index.html";
}

// 🔹 Load Applications
function loadApplications() {
  fetch("http://localhost:8080/api/company/applications", {
    headers: {
      Authorization: "Bearer " + token,
    },
  })
    .then((res) => res.json())
    .then((data) => {
      const content = document.getElementById("content");
      content.innerHTML = "<h3>Applications</h3>";

      if (data.length === 0) {
        content.innerHTML += "<p>No applications yet.</p>";
        return;
      }

      data.forEach((app) => {
        content.innerHTML += `
          <div class="card">
              <h4>${app.studentName}</h4>
              <p>Email: ${app.studentEmail}</p>
              <p>Job: ${app.jobTitle}</p>
              <p class="status ${app.status}">Status: ${app.status}</p>

              <div class="application-actions">
  ${
    app.status === "APPLIED"
      ? `
      <button class="btn-success" onclick="updateApplicationStatus(${app.applicationId}, 'SHORTLISTED')">
          Shortlist
      </button>
      <button class="btn-danger" onclick="updateApplicationStatus(${app.applicationId}, 'REJECTED')">
          Reject
      </button>
      `
      : `
      <button class="btn-disabled" disabled>
          ${app.status}
      </button>
      `
  }
</div>
          </div>
        `;
      });
    });
}

function updateApplicationStatus(applicationId, status) {
  fetch(
    `http://localhost:8080/api/company/applications/${applicationId}?status=${status}`,
    {
      method: "PUT",
      headers: {
        Authorization: "Bearer " + token,
      },
    },
  )
    .then((res) => res.text())
    .then((msg) => {
      alert(msg);
      loadApplications(); // auto refresh
    });
}

// 🔹 Show Create Job Form
function showCreateJob() {
  const content = document.getElementById("content");

  content.innerHTML = `
    <h2>Create New Job</h2>

    <div class="form-section">

      <div class="form-grid">

        <div class="form-group">
          <label>Job Title *</label>
          <input type="text" id="title" required>
        </div>

        <div class="form-group">
          <label>Job Type *</label>
          <select id="jobType">
            <option value="INTERNSHIP">Internship</option>
            <option value="FULL_TIME">Full Time</option>
          </select>
        </div>

        <div class="form-group">
          <label>Location *</label>
          <input type="text" id="location">
        </div>

        <div class="form-group">
          <label>CTC Offered (LPA)</label>
          <input type="number" id="ctc" step="0.1">
        </div>

        <div class="form-group">
          <label>Positions Available</label>
          <input type="number" id="positions">
        </div>

        <div class="form-group">
          <label>Minimum CGPA</label>
          <input type="number" id="minCgpa" step="0.1">
        </div>

        <div class="form-group">
          <label>Eligible Branches (CSE,ISE)</label>
          <input type="text" id="branches">
        </div>

        <div class="form-group">
          <label>Application Deadline *</label>
          <input type="date" id="deadline">
        </div>

      </div>

      <div class="form-group">
        <label>Description</label>
        <textarea id="description"></textarea>
      </div>

      <div class="form-actions">
        <button class="btn-primary" onclick="createJob()">Create Job</button>
        <button class="btn-outline" onclick="loadDashboard()">Cancel</button>
      </div>

    </div>
  `;
}

//crete job
async function createJob() {
  try {
    const title = document.getElementById("title").value.trim();
    const jobType = document.getElementById("jobType").value;
    const location = document.getElementById("location").value.trim();
    const deadline = document.getElementById("deadline").value;

    if (!title || !jobType || !location || !deadline) {
      alert("Please fill all required fields.");
      return;
    }

    const response = await fetch("http://localhost:8080/api/company/jobs", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer " + token,
      },
      body: JSON.stringify({
        title: title,
        description: document.getElementById("description").value,
        jobType: jobType,
        location: location,
        ctcOffered: parseFloat(document.getElementById("ctc").value) || null,
        positionsAvailable:
          parseInt(document.getElementById("positions").value) || null,
        minCgpa: parseFloat(document.getElementById("minCgpa").value) || null,
        eligibleBranches: document.getElementById("branches").value,
        deadline: deadline,
      }),
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText);
    }

    showMessage("Job Created Successfully!", "success");
    loadMyJobs();
  } catch (error) {
    alert("Error: " + error.message);
  }
}

function closeJob(jobId) {
  fetch(`http://localhost:8080/api/company/jobs/${jobId}/close`, {
    method: "PUT",
    headers: {
      Authorization: "Bearer " + token,
    },
  })
    .then((res) => res.text())
    .then((msg) => {
      alert(msg);
      loadMyJobs();
    });
}

function deleteJob(jobId) {
  fetch(`http://localhost:8080/api/company/jobs/${jobId}`, {
    method: "DELETE",
    headers: {
      Authorization: "Bearer " + token,
    },
  })
    .then((res) => res.text())
    .then((msg) => {
      alert(msg);
      loadMyJobs();
    });
}

function loadMyJobs() {
  fetch("http://localhost:8080/api/company/jobs", {
    headers: { Authorization: "Bearer " + token },
  })
    .then((res) => res.json())
    .then((data) => {
      const content = document.getElementById("content");
      content.innerHTML = "<h2>My Jobs</h2>";

      if (data.length === 0) {
        content.innerHTML += "<p>No jobs posted yet.</p>";
        return;
      }

      data.forEach((job) => {
        content.innerHTML += `
          <div class="card">
            <h3>${job.title}</h3>
            <p><strong>Location:</strong> ${job.location || "-"}</p>
            <p><strong>CTC:</strong> ${job.ctcOffered || "-"} LPA</p>
            <p><strong>Positions:</strong> ${job.positionsAvailable || "-"}</p>
            <p><strong>Min CGPA:</strong> ${job.minCgpa || "-"}</p>
            <p><strong>Branches:</strong> ${job.eligibleBranches || "-"}</p>
            <p><strong>Deadline:</strong> ${job.deadline || "-"}</p>
            <p><strong>Status:</strong> ${job.status}</p>

            
            <div class="job-actions">
  ${
    job.status === "OPEN"
      ? `<button class="btn-primary" onclick="closeJob(${job.id})">
             Close Job
         </button>`
      : `<button class="btn-disabled" disabled>
            Closed
         </button>`
  }

  <button class="btn-danger" onclick="deleteJob(${job.id})">
      🗑 Delete
  </button>
</div>
        `;
      });
    });
}

function loadDashboard() {
  fetch("http://localhost:8080/api/company/dashboard", {
    headers: {
      Authorization: "Bearer " + token,
    },
  })
    .then((res) => res.json())
    .then((data) => {
      const content = document.getElementById("content");

      content.innerHTML = `
        <h3>Dashboard</h3>

        <div class="card">
            <h4>Total Jobs</h4>
            <p>${data.totalJobs}</p>
        </div>

        <div class="card">
            <h4>Open Jobs</h4>
            <p>${data.openJobs}</p>
        </div>

        <div class="card">
            <h4>Closed Jobs</h4>
            <p>${data.closedJobs}</p>
        </div>

        <div class="card">
            <h4>Total Applications</h4>
            <p>${data.totalApplications}</p>
        </div>
      `;
    });
}

// loadDashboard();
window.onload = async function () {
  try {
    const response = await fetch("http://localhost:8080/api/company/profile", {
      headers: {
        Authorization: "Bearer " + token,
      },
    });

    const data = await response.json();

    if (!data.profileCompleted) {
      window.location.href = "complete-company-profile.html";
      return;
    }

    loadDashboard();
  } catch (error) {
    console.log("Error checking profile");
  }
};

function showMessage(text, type) {
  const msg = document.getElementById("message");
  msg.className = "message " + type;
  msg.innerText = text;

  setTimeout(() => {
    msg.innerText = "";
    msg.className = "message";
  }, 3000);
}
