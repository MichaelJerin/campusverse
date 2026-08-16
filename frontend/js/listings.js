requireAuth();
renderNavbar("marketplace");

const currentUserName = getUserName();
let currentTab = "browse";

const listingTypeSelect = document.getElementById("listingType");
const priceField = document.getElementById("priceField");

function togglePriceField() {
    priceField.style.display = listingTypeSelect.value === "EXCHANGE" ? "none" : "block";
}
listingTypeSelect.addEventListener("change", togglePriceField);
togglePriceField();

function switchTab(tab) {
    currentTab = tab;
    document.getElementById("tabBrowse").classList.toggle("active", tab === "browse");
    document.getElementById("tabMine").classList.toggle("active", tab === "mine");
    loadListings();
}

async function loadListings() {
    const keyword = document.getElementById("searchKeyword").value;
    const category = document.getElementById("searchCategory").value;
    const minPrice = document.getElementById("minPrice").value;
    const maxPrice = document.getElementById("maxPrice").value;

    const params = new URLSearchParams();
    if (keyword) params.append("keyword", keyword);
    if (category) params.append("category", category);
    if (minPrice) params.append("minPrice", minPrice);
    if (maxPrice) params.append("maxPrice", maxPrice);

    try {
        const listings = await apiFetch("/listings?" + params.toString());

        const filtered = currentTab === "mine"
            ? listings.filter(l => l.sellerName === currentUserName)
            : listings.filter(l => l.sellerName !== currentUserName);

        renderListings(filtered);
    } catch (err) {
        alert(err.message);
    }
}

function priceDisplay(listing) {
    if (listing.listingType === "EXCHANGE") return `<span class="price-tag exchange">Exchange Only</span>`;
    if (listing.price == null) return `<span class="price-tag exchange">Price on request</span>`;
    const suffix = listing.listingType === "EITHER" ? " (or exchange)" : "";
    return `<span class="price-tag">₹${listing.price}</span><span style="font-size:13px; color:var(--text-secondary);">${suffix}</span>`;
}

function renderListings(listings) {
    const container = document.getElementById("listingsList");

    if (listings.length === 0) {
        const emptyText = currentTab === "mine" ? "You haven't listed anything yet." : "No listings match your search yet.";
        container.innerHTML = `<div class="empty-state">${emptyText}</div>`;
        return;
    }

    container.innerHTML = listings.map(l => `
        <div class="card item-card">
            <span class="status-pill status-${l.status.toLowerCase()}">${l.status}</span>
            <h4 class="item-title" style="margin-top:12px;">${l.title}</h4>
            <p class="item-meta">${l.category} · ${l.condition || "Condition N/A"}</p>
            <div style="margin: 10px 0;">${priceDisplay(l)}</div>
            <p class="item-desc">${l.description || ""}</p>
            <p class="item-meta" style="margin-bottom:16px;">${currentTab === "mine" ? "" : "Listed by " + l.sellerName}</p>
            <a href="listing-detail.html?id=${l.id}" class="btn btn-secondary">${currentTab === "mine" ? "Manage" : "View Details"}</a>
        </div>
    `).join("");
}

document.getElementById("postListingForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const payload = {
        title: document.getElementById("title").value,
        description: document.getElementById("description").value,
        category: document.getElementById("category").value,
        listingType: listingTypeSelect.value,
        condition: document.getElementById("condition").value,
        price: listingTypeSelect.value === "EXCHANGE" ? null : (parseFloat(document.getElementById("price").value) || null)
    };

    const form = document.getElementById("postListingForm");
    try {
        await apiFetch("/listings", { method: "POST", body: JSON.stringify(payload) });
        form.reset();
        togglePriceField();
        document.getElementById("postError").style.display = "none";
        switchTab("mine"); // jump to My Listings so they immediately see what they just posted
    } catch (err) {
        const box = document.getElementById("postError");
        box.textContent = err.message;
        box.style.display = "block";
    }
});

loadListings();