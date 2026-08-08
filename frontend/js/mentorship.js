requireAuth();
renderNavbar("mentorship");

const roleSelect = document.getElementById("role");
const ratingField = document.getElementById("ratingField");

function toggleRatingField() {
    ratingField.style.display = roleSelect.value === "MENTOR" ? "block" : "none";
}
roleSelect.addEventListener("change", toggleRatingField);
toggleRatingField();

// ---- Add interest ----
document.getElementById("skillForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    document.getElementById("skillError").style.display = "none";

    const payload = {
        interest: document.getElementById("interest").value,
        role: roleSelect.value,
        selfRating: roleSelect.value === "MENTOR" ? parseInt(document.getElementById("selfRating").value) || null : null
    };

    try {
        await apiFetch("/skills", { method: "POST", body: JSON.stringify(payload) });
        document.getElementById("skillForm").reset();
        toggleRatingField();
        loadMySkills();
    } catch (err) {
        const box = document.getElementById("skillError");
        box.textContent = err.message;
        box.style.display = "block";
    }
});

async function loadMySkills() {
    try {
        const skills = await apiFetch("/skills/me");
        const container = document.getElementById("myInterestsList");

        if (skills.length === 0) {
            container.innerHTML = `<p class="item-meta">You haven't added any interests yet.</p>`;
            return;
        }

        container.innerHTML = skills.map(s => `
            <span class="tag ${s.role === "MENTOR" ? "tag-mentor" : "tag-learner"}">
                ${s.interest} · ${s.role}${s.selfRating ? " · " + s.selfRating + "/5" : ""}
            </span>
        `).join("");
    } catch (err) {
        alert(err.message);
    }
}

// ---- Search mentors ----
let selectedMentor = null;

async function searchMentors() {
    const interest = document.getElementById("searchInterest").value;
    if (!interest.trim()) return;

    try {
        const results = await apiFetch("/skills/mentors?interest=" + encodeURIComponent(interest));
        renderMentorResults(results, interest);
    } catch (err) {
        alert(err.message);
    }
}

function renderMentorResults(results, interest) {
    const container = document.getElementById("mentorResults");

    if (results.length === 0) {
        container.innerHTML = `<div class="empty-state">No mentors found for "${interest}" yet.</div>`;
        return;
    }

    container.innerHTML = results.map(r => `
        <div class="card mentor-card">
            <div class="mentor-card-header">
                <strong>${r.mentorName}</strong>
                <span class="score-badge">Match score: ${r.matchScore}</span>
            </div>
            <p class="item-meta">Self-rating: ${r.selfRating || "N/A"}/5 · ${r.sharedInterestCount} shared interest(s)</p>
            <button class="btn btn-primary" style="margin-top:10px;" onclick='openModal(${r.mentorId}, "${interest}")'>Request Mentorship</button>
        </div>
    `).join("");
}

// ---- Request modal ----
function openModal(mentorId, interest) {
    selectedMentor = { mentorId, interest };
    document.getElementById("requestModal").style.display = "flex";
}

function closeModal() {
    document.getElementById("requestModal").style.display = "none";
    document.getElementById("requestForm").reset();
    document.getElementById("requestError").style.display = "none";
}

document.getElementById("requestForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const payload = {
        mentorId: selectedMentor.mentorId,
        interest: selectedMentor.interest,
        message: document.getElementById("requestMessage").value
    };

    try {
        await apiFetch("/matches", { method: "POST", body: JSON.stringify(payload) });
        closeModal();
        alert("Request sent!");
    } catch (err) {
        const box = document.getElementById("requestError");
        box.textContent = err.message;
        box.style.display = "block";
    }
});

loadMySkills();