import { useQuery, useSubscription } from '@apollo/client/react';
import { useState } from 'react';
import { GET_ACTIVE_ALERTS, ALERT_CREATED_SUBSCRIPTION } from '../graphql/queries';
import type { Alert } from '../types';
import CampusMap from '../components/CampusMap';
import AlertCard from '../components/AlertCard';
import AlertDetailDrawer from '../components/AlertDetailDrawer';

interface ActiveAlertsData {
  activeAlerts: Alert[];
}

interface AlertCreatedData {
  alertCreated: Alert;
}

export default function Dashboard() {
  const { data, loading, error } = useQuery<ActiveAlertsData>(GET_ACTIVE_ALERTS);
  const [selectedAlert, setSelectedAlert] = useState<Alert | null>(null);

  useSubscription<AlertCreatedData>(ALERT_CREATED_SUBSCRIPTION, {
    onData: ({ client, data: subData }) => {
      if (!subData.data?.alertCreated) return;
      const newAlert = subData.data.alertCreated;

      client.cache.updateQuery<ActiveAlertsData>({ query: GET_ACTIVE_ALERTS }, (existing) => {
        if (!existing) return { activeAlerts: [newAlert] };
        const alreadyExists = existing.activeAlerts.some(
          (a) => a.id === newAlert.id
        );
        if (alreadyExists) return existing;
        return { activeAlerts: [newAlert, ...existing.activeAlerts] };
      });
    },
  });

  const alerts: Alert[] = data?.activeAlerts ?? [];

  return (
    <div className="flex h-[calc(100vh-4rem)]">
      {/* Left panel – live alert feed */}
      <div className="w-96 bg-gray-900 border-r border-gray-800 flex flex-col">
        <div className="p-4 border-b border-gray-800">
          <h2 className="text-white font-bold text-lg">Live Alerts</h2>
          <p className="text-gray-400 text-sm">
            {alerts.length} active alert{alerts.length !== 1 && 's'}
          </p>
        </div>

        <div className="flex-1 overflow-y-auto p-3 space-y-3">
          {loading && (
            <div className="text-gray-400 text-center py-8">Loading alerts...</div>
          )}
          {error && (
            <div className="text-red-400 text-center py-8">
              Error loading alerts: {error.message}
            </div>
          )}
          {!loading && alerts.length === 0 && (
            <div className="text-gray-500 text-center py-8">
              No active alerts. Campus is safe!
            </div>
          )}
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

      {/* Right panel – campus map */}
      <div className="flex-1 relative">
        <CampusMap
          alerts={alerts}
          onAlertSelect={(alert) => setSelectedAlert(alert)}
        />
      </div>

      {/* Detail drawer */}
      <AlertDetailDrawer
        alert={selectedAlert}
        onClose={() => setSelectedAlert(null)}
      />
    </div>
  );
}
