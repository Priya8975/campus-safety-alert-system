import { MapContainer, TileLayer, Marker, Popup, useMapEvents } from 'react-leaflet';
import L from 'leaflet';
import type { Alert } from '../types';

const SBU_CENTER: [number, number] = [40.9136, -73.1235];

const severityIcons: Record<string, L.Icon> = {
  CRITICAL: createIcon('#dc2626'),
  HIGH: createIcon('#f97316'),
  MEDIUM: createIcon('#eab308'),
  LOW: createIcon('#3b82f6'),
};

function createIcon(color: string): L.Icon {
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="24" height="36" viewBox="0 0 24 36">
    <path d="M12 0C5.4 0 0 5.4 0 12c0 9 12 24 12 24s12-15 12-24C24 5.4 18.6 0 12 0z" fill="${color}" stroke="#fff" stroke-width="1.5"/>
    <circle cx="12" cy="12" r="5" fill="#fff"/>
  </svg>`;
  return L.icon({
    iconUrl: `data:image/svg+xml;base64,${btoa(svg)}`,
    iconSize: [24, 36],
    iconAnchor: [12, 36],
    popupAnchor: [0, -36],
  });
}

interface CampusMapProps {
  alerts: Alert[];
  onAlertSelect?: (alert: Alert) => void;
  onMapClick?: (lat: number, lng: number) => void;
  clickMarker?: { lat: number; lng: number } | null;
}

function MapClickHandler({ onMapClick }: { onMapClick?: (lat: number, lng: number) => void }) {
  useMapEvents({
    click(e) {
      onMapClick?.(e.latlng.lat, e.latlng.lng);
    },
  });
  return null;
}

export default function CampusMap({ alerts, onAlertSelect, onMapClick, clickMarker }: CampusMapProps) {
  return (
    <MapContainer center={SBU_CENTER} zoom={16} className="h-full w-full rounded-lg">
      <TileLayer
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />
      <MapClickHandler onMapClick={onMapClick} />

      {alerts
        .filter((a) => a.status === 'ACTIVE')
        .map((alert) => (
          <Marker
            key={alert.id}
            position={[alert.location.lat, alert.location.lng]}
            icon={severityIcons[alert.severity] || severityIcons.LOW}
            eventHandlers={{
              click: () => onAlertSelect?.(alert),
            }}
          >
            <Popup>
              <div className="text-sm">
                <strong>{alert.title}</strong>
                <br />
                <span className="text-gray-600">{alert.severity} | {alert.location.zone}</span>
              </div>
            </Popup>
          </Marker>
        ))}

      {clickMarker && (
        <Marker position={[clickMarker.lat, clickMarker.lng]}>
          <Popup>New alert location</Popup>
        </Marker>
      )}
    </MapContainer>
  );
}
