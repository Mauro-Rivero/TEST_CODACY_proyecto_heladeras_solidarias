async function recomendarPuntosHeladeras(){
    const url = "https://74d20d86-99bd-452c-8c65-77ce0eac7fef.mock.pstmn.io/recommendation";
    try {
        const response = await fetch(url);
        const data = await response.json();

        const { Marker } = await google.maps.importLibrary("marker");
        data.puntosRecomendados.forEach(punto => {
                    const puntoRecomendado = new Marker( {
                        position: {lat: punto.latitud, lng: punto.longitud},
                        map: mapa,
                        title: "Punto recomendado",
                        icon: {
                            url: '../assets/iconRecomendacionHeladeras.png',
                            scaledSize: new google.maps.Size(25, 35),
                            origin: new google.maps.Point(0, 0)
                        }
                    })
                    const infoWindow = new google.maps.InfoWindow({
                        content:    `<h4>Punto recomendado</h4>
                                    <p>${punto.direccion}</p>`

                    });

                    puntoRecomendado.addListener('click', () => {
                        infoWindow.open(mapa, puntoRecomendado);
                    });
                })
    }catch(error){}
}
async function obtenerHeladeras() {
    const url = 'https://proyecto-heladeras-solidarias.onrender.com/api/heladeras';
    try {
        const response = await fetch(url);
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

            const infoWindow = new google.maps.InfoWindow( {
                content: `<h4>${heladera.nombre}</h4><p>${heladera.ubicacion.direccion}</p>`
            });

            marker.addListener('click', () => {
                infoWindow.open(mapa, marker);
            });

            heladeras.push({ marker: marker, position: heladera.position });
        });

        return heladeras;
    } catch (error) {
        console.error('Error fetching heladeras data:', error);
    }
}


async function iniciarMapa() {
            const posicion = {lat: -34.5990697, lng: -58.4241739}
            const { Map } = await google.maps.importLibrary("maps");
            mapa = new Map(document.querySelector('.map-recomendacion'), {
                zoom:11,
                center: posicion,
                mapId: "7d27190631ce6b46"
            });
}

document.querySelector('#btn-abrir-mapa').addEventListener("click", function(e) {
    e.preventDefault();
    let mapDisplay = document.querySelector(".map-container").style.display
    document.querySelector(".map-container").style.display = (mapDisplay == 'none')? 'block': 'none';
    iniciarMapa()
    if (document.querySelector('#colocar-heladera')) recomendarPuntosHeladeras();
    obtenerHeladeras()
    if (!Map) {
        iniciarMapa();
    }})