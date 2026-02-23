import { useQuery } from '@apollo/client/react';
import { useState } from 'react';
import { GET_ACTIVE_ALERTS } from '../graphql/queries';
import type { Alert } from '../types';
import CampusMap from '../components/CampusMap';
import CreateAlertForm from '../components/CreateAlertForm';
import AlertCard from '../components/AlertCard';
import AlertDetailDrawer from '../components/AlertDetailDrawer';

interface ActiveAlertsData {
  activeAlerts: Alert[];
}

export default function AdminPanel() {
  const { data } = useQuery<ActiveAlertsData>(GET_ACTIVE_ALERTS);
  const [clickMarker, setClickMarker] = useState<{ lat: number; lng: number } | null>(null);
  const [selectedAlert, setSelectedAlert] = useState<Alert | null>(null);

  const alerts: Alert[] = data?.activeAlerts ?? [];

  return (
    <div className="flex h-[calc(100vh-4rem)]">
      {/* Left panel – create alert form */}
      <div className="w-96 bg-gray-900 border-r border-gray-800 flex flex-col">
        <div className="p-4 border-b border-gray-800">
          <h2 className="text-white font-bold text-lg">Create Alert</h2>
          <p className="text-gray-400 text-sm">Click the map to set the incident location</p>
        </div>

        <div className="flex-1 overflow-y-auto p-4">
          <CreateAlertForm lat={clickMarker?.lat ?? null} lng={clickMarker?.lng ?? null} />

          <div className="mt-6 border-t border-gray-700 pt-4">
            <h3 className="text-gray-300 font-medium text-sm mb-3">
              Active Alerts ({alerts.length})
            </h3>
            <div className="space-y-2">
              {alerts.map((alert) => (
                <AlertCard
                  key={alert.id}
                  alert={alert}
                  onClick={() => setSelectedAlert(alert)}
                  isSelected={selectedAlert?.id === alert.id}
                />
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* Right panel – map */}
      <div className="flex-1 relative">
        <CampusMap
          alerts={alerts}
          onAlertSelect={(alert) => setSelectedAlert(alert)}
          onMapClick={(lat, lng) => setClickMarker({ lat, lng })}
          clickMarker={clickMarker}
        />
      </div>

      <AlertDetailDrawer
        alert={selectedAlert}
        onClose={() => setSelectedAlert(null)}
      />
    </div>
  );
}
