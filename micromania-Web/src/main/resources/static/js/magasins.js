(function () {
  document.addEventListener('DOMContentLoaded', function () {
    const mapElement = document.getElementById('magasins-map');
    if (!mapElement || typeof L === 'undefined') {
      return;
    }

    const cardElements = Array.from(document.querySelectorAll('.store-card[data-store-id]'));
    const selectedStoreId = mapElement.dataset.selectedStoreId || null;

    const stores = cardElements
      .map(function (card) {
        const latitude = parseFloat(card.dataset.latitude || '');
        const longitude = parseFloat(card.dataset.longitude || '');
        if (Number.isNaN(latitude) || Number.isNaN(longitude)) {
          return null;
        }

        return {
          id: card.dataset.storeId,
          name: card.dataset.storeName || '',
          address: card.dataset.storeAddress || '',
          latitude: latitude,
          longitude: longitude,
          card: card,
          focusButton: card.querySelector('.js-store-focus')
        };
      })
      .filter(Boolean);

    if (!stores.length) {
      mapElement.innerHTML = '<div class="store-map-empty">Coordonnées indisponibles pour afficher la carte.</div>';
      return;
    }

    const defaultStore = stores.find(function (store) { return store.id === selectedStoreId; }) || stores[0];
    const map = L.map(mapElement, {
      scrollWheelZoom: false,
      zoomControl: true
    }).setView([defaultStore.latitude, defaultStore.longitude], 10);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap contributors',
      maxZoom: 19
    }).addTo(map);

    const markersById = new Map();
    const cardsById = new Map();
    let activeStoreId = selectedStoreId || defaultStore.id;

    function markerStyle(storeId) {
      const isActive = storeId === activeStoreId;
      const isSelected = storeId === selectedStoreId;

      return {
        radius: isActive ? 11 : 9,
        weight: 2,
        color: isSelected ? '#00ff88' : '#00d4ff',
        fillColor: isSelected ? '#00ff88' : '#00d4ff',
        fillOpacity: isActive ? 0.9 : 0.68
      };
    }

    function escapeHtml(value) {
      return String(value || '')
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
    }

    function popupHtml(store) {
      return (
        '<div class="store-popup">' +
          '<div class="store-popup-title">' + escapeHtml(store.name) + '</div>' +
          '<div class="store-popup-address">' + escapeHtml(store.address) + '</div>' +
          '<a class="store-popup-link" href="#store-card-' + escapeHtml(store.id) + '">Voir la fiche</a>' +
        '</div>'
      );
    }

    function refreshMarkers() {
      markersById.forEach(function (marker, id) {
        marker.setStyle(markerStyle(id));
      });
    }

    function refreshCards() {
      cardsById.forEach(function (card, id) {
        card.classList.toggle('store-card-map-active', id === activeStoreId);
      });
    }

    function activateStore(storeId, options) {
      const settings = Object.assign({
        openPopup: false,
        scrollToCard: false,
        recenter: true
      }, options || {});

      activeStoreId = storeId;
      refreshMarkers();
      refreshCards();

      const marker = markersById.get(storeId);
      const card = cardsById.get(storeId);

      if (marker && settings.recenter) {
        map.flyTo(marker.getLatLng(), Math.max(map.getZoom(), 11), {
          duration: 0.45,
          animate: true
        });
      }
      if (marker && settings.openPopup) {
        marker.openPopup();
      }
      if (card && settings.scrollToCard) {
        card.scrollIntoView({ behavior: 'smooth', block: 'center' });
      }
    }

    stores.forEach(function (store) {
      cardsById.set(store.id, store.card);

      const marker = L.circleMarker([store.latitude, store.longitude], markerStyle(store.id))
        .bindPopup(popupHtml(store))
        .addTo(map);

      marker.on('click', function () {
        activateStore(store.id, { openPopup: true, scrollToCard: true, recenter: false });
      });

      markersById.set(store.id, marker);

      if (store.focusButton) {
        store.focusButton.addEventListener('click', function () {
          activateStore(store.id, { openPopup: true, scrollToCard: false, recenter: true });
          mapElement.scrollIntoView({ behavior: 'smooth', block: 'center' });
        });
      }

      store.card.addEventListener('mouseenter', function () {
        activateStore(store.id, { openPopup: false, scrollToCard: false, recenter: false });
      });
    });

    const bounds = L.latLngBounds(stores.map(function (store) {
      return [store.latitude, store.longitude];
    }));

    if (stores.length > 1) {
      map.fitBounds(bounds, { padding: [30, 30] });
    } else {
      map.setView([defaultStore.latitude, defaultStore.longitude], 12);
    }

    activateStore(activeStoreId, { openPopup: false, scrollToCard: false, recenter: false });

    requestAnimationFrame(function () {
      map.invalidateSize();
      setTimeout(function () {
        map.invalidateSize();
      }, 180);
    });
  });
})();
