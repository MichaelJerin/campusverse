requireAuth();
renderNavbar("businesses");

function starString(avg) {
    if (avg === null || avg === undefined) return `<span class="rating-count">No reviews yet</span>`;
    const full = Math.round(avg);
    return `<span class="stars">${"★".repeat(full)}${"☆".repeat(5 - full)}</span> <span class="rating-count">${avg} (${arguments[1] || ""})</span>`;
}

async function loadBusinesses() {
    const keyword = document.getElementById("searchKeyword").value;
    const category = document.getElementById("searchCategory").value;
    const tag = document.getElementById("searchTag").value;

    const params = new URLSearchParams();
    if (keyword) params.append("keyword", keyword);
    if (category) params.append("category", category);
    if (tag) params.append("tag", tag);

    try {
        const businesses = await apiFetch("/businesses?" + params.toString());
        renderBusinesses(businesses);
    } catch (err) {
        alert(err.message);
    }
}

function renderBusinesses(businesses) {
    const container = document.getElementById("businessList");

    if (businesses.length === 0) {
        container.innerHTML = `<div class="empty-state">No listings match your search yet.</div>`;
        return;
    }

    container.innerHTML = businesses.map(b => {
        const ratingHtml = b.averageRating
            ? `<span class="stars">${"★".repeat(Math.round(b.averageRating))}${"☆".repeat(5 - Math.round(b.averageRating))}</span> <span class="rating-count">${b.averageRating} (${b.reviewCount})</span>`
            : `<span class="rating-count">No reviews yet</span>`;

        const tags = (b.tags || "").split(",").filter(t => t.trim()).map(t => `<span class="tag-chip">${t.trim()}</span>`).join("");

        return `
            <div class="card item-card">
                <h4 class="item-title">${b.name}</h4>
                <p class="item-meta">${b.category} · ${b.address}</p>
                <div class="rating-row">${ratingHtml}</div>
                <p class="item-desc">${b.description || ""}</p>
                <div style="margin-bottom:14px;">${tags}</div>
                <div style="display:flex; gap:10px;">
                    <a href="business-detail.html?id=${b.id}" class="btn btn-secondary">View Details</a>
                    ${b.mapLink ? `<a href="${b.mapLink}" target="_blank" rel="noopener" class="btn btn-ghost">📍 Map</a>` : ""}
                </div>
            </div>
        `;
    }).join("");
}

document.getElementById("postBusinessForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const payload = {
        name: document.getElementById("name").value,
        description: document.getElementById("description").value,
        category: document.getElementById("category").value,
        address: document.getElementById("address").value,
        tags: document.getElementById("tags").value
    };

    const form = document.getElementById("postBusinessForm");
    try {
        await apiFetch("/businesses", { method: "POST", body: JSON.stringify(payload) });
        form.reset();
        document.getElementById("postError").style.display = "none";
        loadBusinesses();
    } catch (err) {
        const box = document.getElementById("postError");
        box.textContent = err.message;
        box.style.display = "block";
    }
});

loadBusinesses();