requireAuth();
renderNavbar("lostfound");

const params = new URLSearchParams(window.location.search);
const itemId = params.get("id");
const currentUserName = getUserName();

async function loadItem() {
    try {
        const item = await apiFetch("/items/" + itemId);
        renderItem(item);

        const isFinder = item.finderName === currentUserName;

        if (isFinder) {
            document.getElementById("claimsListSection").style.display = "block";
            loadClaims();
        } else if (item.status === "OPEN") {
            document.getElementById("claimSection").style.display = "block";
        }
    } catch (err) {
        alert(err.message);
    }
}

function renderItem(item) {
    document.getElementById("itemCard").innerHTML = `
        <span class="status-pill status-${item.status.toLowerCase()}">${item.status}</span>
        <h2 style="margin-top:14px; margin-bottom:4px;">${item.title}</h2>
        <p class="item-meta" style="margin-bottom:16px;">${item.category || "Uncategorized"} · ${item.location}</p>
        <p style="margin-bottom:16px; line-height:1.6;">${item.description || "No description provided."}</p>
        <p class="item-meta">Contact: ${item.contactNumber}</p>
        <p class="item-meta">Found on: ${item.eventDate || "N/A"}</p>
        <p class="item-meta">Posted by ${item.finderName}</p>
    `;
}

const claimForm = document.getElementById("claimForm");
claimForm?.addEventListener("submit", async (e) => {
    e.preventDefault();
    const message = document.getElementById("claimMessage").value;

    try {
        await apiFetch(`/items/${itemId}/claims`, {
            method: "POST",
            body: JSON.stringify({ message })
        });
        window.location.href = "items.html";
    } catch (err) {
        const box = document.getElementById("claimError");
        box.textContent = err.message;
        box.style.display = "block";
    }
});

async function loadClaims() {
    try {
        const claims = await apiFetch(`/items/${itemId}/claims`);
        renderClaims(claims);
    } catch (err) {
        alert(err.message);
    }
}

function renderClaims(claims) {
    const container = document.getElementById("claimsList");

    if (claims.length === 0) {
        container.innerHTML = `<div class="empty-state">No claims yet.</div>`;
        return;
    }

    container.innerHTML = claims.map(claim => `
        <div class="card" style="margin-bottom: 14px; padding: 20px;">
            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:8px;">
                <strong>${claim.claimantName}</strong>
                <span class="status-pill status-${claim.status.toLowerCase()}">${claim.status}</span>
            </div>
            <p style="margin-bottom: ${claim.status === "PENDING" ? "14px" : "0"};">${claim.message}</p>
            ${claim.status === "PENDING" ? `<button class="btn btn-success" onclick="approveClaim(${claim.id})">Approve Claim</button>` : ""}
        </div>
    `).join("");
}

async function approveClaim(claimId) {
    if (!confirm("Approve this claim? This will reject all other claims and close the item.")) return;

    try {
        await apiFetch(`/claims/${claimId}/approve`, { method: "POST" });
        loadItem();
        loadClaims();
    } catch (err) {
        alert(err.message);
    }
}

loadItem();