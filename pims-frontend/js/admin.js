const BASE_URL = "https://pims-backend-xa8s.onrender.com";
const token = localStorage.getItem("token");

if (!token) {
    window.location.href = "index.html";
}

fetch(`${BASE_URL}/api/admin/companies`, {
    headers: {
        "Authorization": "Bearer " + token
    }
})
.then(res => res.json())
.then(data => {
    const table = document.getElementById("companyTable");

    data.forEach(company => {
        const row = document.createElement("tr");


        row.innerHTML = `
            <td>${company.id}</td>
            <td>${company.companyName}</td>
            <td>
    ${company.approved 
        ? '<span style="color: #22c55e; font-weight: bold;">✔ Approved</span>' 
        : '<span style="color: #f59e0b; font-weight: bold;">Pending</span>'
    }
</td>

<td>
    ${
        company.approved
        ? '<button disabled style="background: #22c55e;">Approved</button>'
        : `
            <button onclick="approve(${company.id})" style="background:#22c55e;">Approve</button>
            <button onclick="reject(${company.id})" style="background:#ef4444;">Reject</button>
          `
    }
</td>

        `;

        table.appendChild(row);
    });
});

function approve(id) {
    fetch(`${BASE_URL}/api/admin/company/${id}/approve`, {
        method: "PUT",
        headers: {
            "Authorization": "Bearer " + token
        }
    }).then(res => res.text())
    .then(msg => {
        alert("Company Approved Successfully ✅");
        location.reload();
    });
}

function reject(id) {
    fetch(`${BASE_URL}/api/admin/company/${id}/reject`, {
        method: "PUT",
        headers: {
            "Authorization": "Bearer " + token
        }
    }).then(() => location.reload());
}

function logout() {
    localStorage.removeItem("token");
    window.location.href = "index.html";
}