const token = localStorage.getItem("token");

async function saveStudentProfile() {

    const response = await fetch("http://localhost:8080/api/student/profile", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + token
        },
        body: JSON.stringify({
            fullName: document.getElementById("fullName").value,
            usn: document.getElementById("usn").value,
            branch: document.getElementById("branch").value,
            cgpa: parseFloat(document.getElementById("cgpa").value),
            graduationYear: parseInt(document.getElementById("graduationYear").value)
        })
    });

    if (!response.ok) {
        alert("Error saving profile");
        return;
    }

    alert("Profile saved successfully!");

    window.location.href = "student.html";
}
