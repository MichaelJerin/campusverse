requireAuth();
renderNavbar("lostfound");

async function loadItems() {
    const keyword = document.getElementById("searchKeyword").value;
    const location = document.getElementById("searchLocation").value;
    const category = document.getElementById("searchCategory").value;
    const eventDate = document.getElementById("searchDate").value;

    const params = new URLSearchParams();
    if (keyword) params.append("keyword", keyword);
    if (location) params.append("location", location);
    if (category) params.append("category", category);
    if (eventDate) params.append("eventDate", eventDate);

    try {
        const items = await apiFetch("/items?" + params.toString());
        renderItems(items);
    } catch (err) {
        alert(err.message);
    }
}

function renderItems(items) {
    const container = document.getElementById("itemsList");

    if (items.length === 0) {
        container.innerHTML = `<div class="empty-state">No items match your search yet.</div>`;
        return;
    }

    container.innerHTML = items.map(item => `
        <div class="card item-card">
            <span class="status-pill status-${item.status.toLowerCase()}">${item.status}</span>
            <h4 class="item-title" style="margin-top:12px;">${item.title}</h4>
            <p class="item-meta">${item.category || "Uncategorized"} · ${item.location}</p>
            <p class="item-desc">${item.description || ""}</p>
            <p class="item-meta" style="margin-bottom:16px;">Posted by ${item.finderName}</p>
            <a href="item-detail.html?id=${item.id}" class="btn btn-secondary">View Details</a>
        </div>
    `).join("");
}

document.getElementById("postItemForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    hideError();

    const payload = {
        title: document.getElementById("title").value,
        description: document.getElementById("description").value,
        category: document.getElementById("category").value,
        location: document.getElementById("location").value,
        contactNumber: document.getElementById("contactNumber").value,
        eventDate: document.getElementById("eventDate").value || null
    };

    const form = document.getElementById("postItemForm");
    try {
        await apiFetch("/items", { method: "POST", body: JSON.stringify(payload) });
        form.reset();
        document.getElementById("postError").style.display = "none";
        loadItems();
    } catch (err) {
        const box = document.getElementById("postError");
        box.textContent = err.message;
        box.style.display = "block";
    }
});

loadItems();