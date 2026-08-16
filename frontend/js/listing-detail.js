requireAuth();
renderNavbar("marketplace");

const params = new URLSearchParams(window.location.search);
const listingId = params.get("id");
const currentUserName = getUserName();
let currentListing = null;

async function loadListing() {
    try {
        currentListing = await apiFetch("/listings/" + listingId);
        renderListing(currentListing);

        const isSeller = currentListing.sellerName === currentUserName;

        if (isSeller) {
            document.getElementById("requestsListSection").style.display = "block";
            loadRequests();
        } else if (currentListing.status === "AVAILABLE") {
            document.getElementById("requestSection").style.display = "block";
        }
    } catch (err) {
        alert(err.message);
    }
}

function priceDisplay(l) {
    if (l.listingType === "EXCHANGE") return `<span class="price-tag exchange">Exchange Only</span>`;
    if (l.price == null) return `<span class="price-tag exchange">Price on request</span>`;
    const suffix = l.listingType === "EITHER" ? " (or exchange)" : "";
    return `<span class="price-tag">₹${l.price}</span><span style="font-size:13px; color:var(--text-secondary);">${suffix}</span>`;
}

function renderListing(l) {
    const isSeller = l.sellerName === currentUserName;

    document.getElementById("listingCard").innerHTML = `
        <span class="status-pill status-${l.status.toLowerCase()}">${l.status}</span>
        <h2 style="margin-top:14px; margin-bottom:4px;">${l.title}</h2>
        <p class="item-meta" style="margin-bottom:12px;">${l.category} · ${l.condition || "Condition N/A"}</p>
        <div style="margin-bottom:14px;">${priceDisplay(l)}</div>
        <p style="margin-bottom:16px; line-height:1.6;">${l.description || "No description provided."}</p>
        <p class="item-meta">Listed by ${l.sellerName}</p>
        ${isSeller ? `
            <div style="display:flex; gap:10px; margin-top:16px;">
                <button class="btn btn-secondary" onclick="startEdit()">Edit</button>
                ${l.status !== "SOLD" ? `<button class="btn btn-primary" onclick="markSold()">Mark as Sold</button>` : ""}
                <button class="btn btn-ghost" onclick="deleteListing()">Delete</button>
            </div>
        ` : ""}
    `;
}

function startEdit() {
    document.getElementById("editTitle").value = currentListing.title;
    document.getElementById("editCategory").value = currentListing.category;
    document.getElementById("editDescription").value = currentListing.description || "";
    document.getElementById("editListingType").value = currentListing.listingType;
    document.getElementById("editCondition").value = currentListing.condition || "USED";
    document.getElementById("editPrice").value = currentListing.price || "";

    document.getElementById("viewMode").style.display = "none";
    document.getElementById("editMode").style.display = "block";
}

function cancelEdit() {
    document.getElementById("viewMode").style.display = "block";
    document.getElementById("editMode").style.display = "none";
}

document.getElementById("editForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const type = document.getElementById("editListingType").value;
    const payload = {
        title: document.getElementById("editTitle").value,
        category: document.getElementById("editCategory").value,
        description: document.getElementById("editDescription").value,
        listingType: type,
        condition: document.getElementById("editCondition").value,
        price: type === "EXCHANGE" ? null : (parseFloat(document.getElementById("editPrice").value) || null)
    };

    try {
        await apiFetch("/listings/" + listingId, { method: "PUT", body: JSON.stringify(payload) });
        cancelEdit();
        loadListing();
    } catch (err) {
        const box = document.getElementById("editError");
        box.textContent = err.message;
        box.style.display = "block";
    }
});

async function markSold() {
    if (!confirm("Mark this listing as sold? This can't be undone.")) return;
    try {
        await apiFetch(`/listings/${listingId}/mark-sold`, { method: "PUT" });
        loadListing();
    } catch (err) {
        alert(err.message);
    }
}

async function deleteListing() {
    if (!confirm("Delete this listing permanently?")) return;
    try {
        await apiFetch(`/listings/${listingId}`, { method: "DELETE" });
        window.location.href = "listings.html";
    } catch (err) {
        alert(err.message);
    }
}

document.getElementById("requestForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const message = document.getElementById("requestMessage").value;

    try {
        await apiFetch(`/listings/${listingId}/requests`, {
            method: "POST",
            body: JSON.stringify({ message })
        });
        window.location.href = "listings.html";
    } catch (err) {
        const box = document.getElementById("requestError");
        box.textContent = err.message;
        box.style.display = "block";
    }
});

async function loadRequests() {
    try {
        const requests = await apiFetch(`/listings/${listingId}/requests`);
        renderRequests(requests);
    } catch (err) {
        alert(err.message);
    }
}

function renderRequests(requests) {
    const container = document.getElementById("requestsList");

    if (requests.length === 0) {
        container.innerHTML = `<div class="empty-state">No requests yet.</div>`;
        return;
    }

    container.innerHTML = requests.map(r => {
        const phone = r.status === "APPROVED" && r.contactPhoneNumber
            ? `<div class="phone-reveal">📞 Contact: ${r.contactPhoneNumber}</div>`
            : "";

        const approveBtn = r.status === "PENDING"
            ? `<button class="btn btn-success" style="margin-top:10px;" onclick="approveRequest(${r.id})">Approve</button>`
            : "";

        return `
            <div class="card" style="margin-bottom: 14px; padding: 20px;">
                <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:8px;">
                    <strong>${r.buyerName}</strong>
                    <span class="status-pill status-${r.status.toLowerCase()}">${r.status}</span>
                </div>
                <p style="margin-bottom: ${approveBtn || phone ? "10px" : "0"};">${r.message || "No message provided."}</p>
                ${phone}
                ${approveBtn}
            </div>
        `;
    }).join("");
}

async function approveRequest(requestId) {
    if (!confirm("Approve this request? This will reject all other requests and mark the listing reserved.")) return;

    try {
        await apiFetch(`/requests/${requestId}/approve`, { method: "PUT" });
        loadListing();
        loadRequests();
    } catch (err) {
        alert(err.message);
    }
}

loadListing();