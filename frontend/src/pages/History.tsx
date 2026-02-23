import { useQuery } from '@apollo/client/react';
import { useState } from 'react';
import { GET_ALERT_HISTORY } from '../graphql/queries';
import type { Alert } from '../types';
import AlertCard from '../components/AlertCard';
import AlertDetailDrawer from '../components/AlertDetailDrawer';

interface AlertHistoryData {
  alertHistory: Alert[];
}

const SEVERITY_OPTIONS = ['ALL', 'CRITICAL', 'HIGH', 'MEDIUM', 'LOW'] as const;
const PAGE_SIZE = 20;

export default function History() {
  const [severityFilter, setSeverityFilter] = useState<string>('ALL');
  const [limit, setLimit] = useState(PAGE_SIZE);
  const [selectedAlert, setSelectedAlert] = useState<Alert | null>(null);

  const { data, loading, error } = useQuery<AlertHistoryData>(GET_ALERT_HISTORY, {
    variables: { limit },
  });

  const allAlerts: Alert[] = data?.alertHistory ?? [];
  const filtered =
    severityFilter === 'ALL'
      ? allAlerts
      : allAlerts.filter((a) => a.severity === severityFilter);

  return (
    <div className="max-w-4xl mx-auto py-6 px-4">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-white text-2xl font-bold">Alert History</h1>
          <p className="text-gray-400 text-sm">
            Showing {filtered.length} of {allAlerts.length} alerts
          </p>
        </div>

        {/* Severity filter */}
        <div className="flex gap-2">
          {SEVERITY_OPTIONS.map((s) => (
            <button
              key={s}
              onClick={() => setSeverityFilter(s)}
              className={`px-3 py-1.5 rounded-lg text-xs font-bold transition-colors ${
                severityFilter === s
                  ? 'bg-red-600 text-white'
                  : 'bg-gray-800 text-gray-400 hover:bg-gray-700'
              }`}
            >
              {s}
            </button>
          ))}
        </div>
      </div>

      {loading && (
        <div className="text-gray-400 text-center py-12">Loading history...</div>
      )}
      {error && (
        <div className="text-red-400 text-center py-12">
          Error: {error.message}
        </div>
      )}

      <div className="space-y-3">
        {filtered.map((alert) => (
          <AlertCard
            key={alert.id}
            alert={alert}
            onClick={() => setSelectedAlert(alert)}
            isSelected={selectedAlert?.id === alert.id}
          />
        ))}
      </div>

      {!loading && filtered.length === 0 && (
        <div className="text-gray-500 text-center py-12">No alerts found.</div>
      )}

      {allAlerts.length >= limit && (
        <div className="text-center mt-6">
          <button
            onClick={() => setLimit((l) => l + PAGE_SIZE)}
            className="px-6 py-2 bg-gray-800 text-gray-300 rounded-lg hover:bg-gray-700 transition-colors text-sm"
          >
            Load More
          </button>
        </div>
      )}

      <AlertDetailDrawer
        alert={selectedAlert}
        onClose={() => setSelectedAlert(null)}
      />
    </div>
  );
}
