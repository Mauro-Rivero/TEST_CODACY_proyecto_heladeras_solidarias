let mapa;
let marker;
let infoWindowActual;  // Variable global para la InfoWindow activa
let heladeras = [];

async function iniciarMapa() {
    const posicion = { lat: -34.5990697, lng: -58.4241739 };

    const { Map } = await google.maps.importLibrary("maps");
    const { Marker } = await google.maps.importLibrary("marker");
    const { Autocomplete } = await google.maps.importLibrary("places");
    const { spherical } = await google.maps.importLibrary("geometry");

    mapa = new Map(document.querySelector('#map'), {
        zoom: 11,
        center: posicion,
        mapId: "7d27190631ce6b46"
    });

    marker = new Marker( {
        map: mapa,
        title: "Posición de búsqueda",
        visible: false
    });

    heladeras = await obtenerHeladeras();

    let autocomplete = document.querySelector('#autocomplete');
    const search = new Autocomplete(autocomplete);
    search.bindTo('bounds', mapa);

    search.addListener('place_changed', function () {
        const place = search.getPlace();
        if (!place.geometry || !place.geometry.location) {
            window.alert("Error al mostrar el lugar");
            return;
        }

        // Cerrar la InfoWindow actual, si hay una abierta
        if (infoWindowActual) {
            infoWindowActual.close();
        }

        marker.setPosition(place.geometry.location);
        marker.setVisible(true);  // Mostrar el marcador
        mapa.setCenter(place.geometry.location);
        mapa.setZoom(14);

        const heladeraMasCerca = encontrarHeladeraMasCercana(place.geometry.location, heladeras, spherical);

        if (heladeraMasCerca) {
            const infoWindow = new google.maps.InfoWindow( {
                content: `<h4>Heladera más cercana</h4><p>${heladeraMasCerca.marker.getTitle()}</p>`
            });

            // Abrir la nueva InfoWindow
            infoWindow.open(mapa, heladeraMasCerca.marker);

            // Asignar la InfoWindow actual
            infoWindowActual = infoWindow;
        }
    });
}

function encontrarHeladeraMasCercana(userPosition, heladeras, spherical) {
    let minDistancia = Infinity;
    let heladeraMasCercana = null;

    heladeras.forEach(heladera => {
        // Calcular la distancia entre la posición del usuario y cada heladera
        const distancia = spherical.computeDistanceBetween(
            userPosition,
            new google.maps.LatLng(heladera.position)
        );

        if (distancia < minDistancia) {
            minDistancia = distancia;
            heladeraMasCercana = heladera;
        }
    });

    return heladeraMasCercana;
}

async function obtenerHeladeras() {
    try {
        const response = await fetch('https://proyecto-heladeras-solidarias.onrender.com/api/heladeras');
        const heladeras = await response.json();

        const { Marker } = await google.maps.importLibrary("marker");

        heladeras.forEach(heladera => {
            const marker = new Marker({
                position: { lat: heladera.ubicacion.latitud, lng: heladera.ubicacion.longitud},
                map: mapa,
                title: heladera.nombre,
                icon: {
                    url: 'https://raw.githubusercontent.com/luciacosenza/proyecto_heladeras_solidarias/main/src/main/resources/static/assets/iconUbicacionHeladeras.png',
                    scaledSize: new google.maps.Size(25, 35),
                    origin: new google.maps.Point(0, 0)
                }
            });

            const infoWindow = new google.maps.InfoWindow({
                content: `<h4>${heladera.nombre}</h4><p>${heladera.ubicacion.direccion}</p>`
            });

            marker.addListener('click', () => {
                // Cerrar cualquier InfoWindow abierta
                if (infoWindowActual) {
                    infoWindowActual.close();
                }

                // Abrir la nueva InfoWindow
                infoWindow.open(mapa, marker);

                // Actualizar la referencia a la InfoWindow abierta
                infoWindowActual = infoWindow;
            });
        });

        return heladeras;
    } catch (error) {
        console.error('Error fetching heladeras data:', error);
    }
}
iniciarMapa();
