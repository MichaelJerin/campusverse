requireAuth();
renderNavbar("businesses");

const params = new URLSearchParams(window.location.search);
const businessId = params.get("id");
let currentBusiness = null;

async function loadBusiness() {
    try {
        currentBusiness = await apiFetch("/businesses/" + businessId);
        renderBusiness(currentBusiness);
        loadReviews();
    } catch (err) {
        alert(err.message);
    }
}

function renderBusiness(b) {
    const ratingHtml = b.averageRating
        ? `<span class="stars">${"★".repeat(Math.round(b.averageRating))}${"☆".repeat(5 - Math.round(b.averageRating))}</span> <span class="rating-count">${b.averageRating} (${b.reviewCount} review${b.reviewCount === 1 ? "" : "s"})</span>`
        : `<span class="rating-count">No reviews yet</span>`;

    const tags = (b.tags || "").split(",").filter(t => t.trim()).map(t => `<span class="tag-chip">${t.trim()}</span>`).join("");

    document.getElementById("businessCard").innerHTML = `
        <h2 style="margin-bottom:4px;">${b.name}</h2>
        <p class="item-meta" style="margin-bottom:12px;">${b.category} · ${b.address}</p>
        <div class="rating-row">${ratingHtml}</div>
        <p style="margin-bottom:14px; line-height:1.6;">${b.description || "No description yet."}</p>
        <div style="margin-bottom:14px;">${tags}</div>
        ${b.mapLink ? `<a href="${b.mapLink}" target="_blank" rel="noopener" class="btn btn-secondary" style="margin-bottom:14px; display:inline-block;">📍 View on Google Maps</a>` : `<p class="item-meta" style="margin-bottom:14px;">No map link added yet.</p>`}
        <p class="item-meta">Added by ${b.createdByName}${b.lastEditedByName && b.lastEditedByName !== b.createdByName ? ` · last edited by ${b.lastEditedByName}` : ""}</p>
        <div style="display:flex; gap:16px; margin-top:16px; align-items:center;">
            <button class="btn btn-secondary" onclick="startEdit()">Edit Listing</button>
            <span class="report-link" onclick="reportBusiness()">Report this listing</span>
        </div>
    `;
}

function startEdit() {
    document.getElementById("editName").value = currentBusiness.name;
    document.getElementById("editCategory").value = currentBusiness.category;
    document.getElementById("editDescription").value = currentBusiness.description || "";
    document.getElementById("editAddress").value = currentBusiness.address;
    document.getElementById("editTags").value = currentBusiness.tags || "";
    document.getElementById("editMapLink").value = currentBusiness.mapLink || "";

    document.getElementById("viewMode").style.display = "none";
    document.getElementById("editMode").style.display = "block";
}

function cancelEdit() {
    document.getElementById("viewMode").style.display = "block";
    document.getElementById("editMode").style.display = "none";
}

document.getElementById("editForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const payload = {
        name: document.getElementById("editName").value,
        category: document.getElementById("editCategory").value,
        description: document.getElementById("editDescription").value,
        address: document.getElementById("editAddress").value,
        tags: document.getElementById("editTags").value,
        mapLink: document.getElementById("editMapLink").value
    };

    try {
        await apiFetch("/businesses/" + businessId, { method: "PUT", body: JSON.stringify(payload) });
        cancelEdit();
        loadBusiness();
    } catch (err) {
        const box = document.getElementById("editError");
        box.textContent = err.message;
        box.style.display = "block";
    }
});

async function reportBusiness() {
    if (!confirm("Report this listing as inaccurate or inappropriate?")) return;

    try {
        await apiFetch("/businesses/" + businessId + "/report", { method: "POST" });
        alert("Thanks — this listing has been reported.");
    } catch (err) {
        alert(err.message);
    }
}

document.getElementById("reviewForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const payload = {
        rating: parseInt(document.getElementById("rating").value),
        comment: document.getElementById("comment").value
    };

    const form = document.getElementById("reviewForm");
    try {
        await apiFetch(`/businesses/${businessId}/reviews`, { method: "POST", body: JSON.stringify(payload) });
        form.reset();
        document.getElementById("reviewError").style.display = "none";
        loadBusiness();
    } catch (err) {
        const box = document.getElementById("reviewError");
        box.textContent = err.message;
        box.style.display = "block";
    }
});

async function loadReviews() {
    try {
        const reviews = await apiFetch(`/businesses/${businessId}/reviews`);
        renderReviews(reviews);
    } catch (err) {
        alert(err.message);
    }
}

function renderReviews(reviews) {
    const container = document.getElementById("reviewsList");

    if (reviews.length === 0) {
        container.innerHTML = `<div class="empty-state">No reviews yet — be the first.</div>`;
        return;
    }

    container.innerHTML = reviews.map(r => `
        <div class="review-item">
            <div class="review-header">
                <strong>${r.reviewerName}</strong>
                <span class="stars">${"★".repeat(r.rating)}${"☆".repeat(5 - r.rating)}</span>
            </div>
            <p>${r.comment || ""}</p>
        </div>
    `).join("");
}

loadBusiness();