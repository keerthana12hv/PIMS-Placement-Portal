const BASE_URL = "https://pims-backend-xa8s.onrender.com";
const token = localStorage.getItem("token");

async function saveCompanyProfile() {

    const response = await fetch(`${BASE_URL}/api/company/profile`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + token
        },
        body: JSON.stringify({
            companyName: document.getElementById("companyName").value,
            description: document.getElementById("description").value,
            website: document.getElementById("website").value,
            contactPerson: document.getElementById("contactPerson").value,
            contactEmail: document.getElementById("contactEmail").value
        })
    });

    if (!response.ok) {
        alert("Error saving company profile");
        return;
    }

    alert("Profile submitted! Waiting for admin approval.");

    window.location.href = "company.html";
}
