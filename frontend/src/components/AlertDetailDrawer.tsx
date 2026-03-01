import type { Alert } from '../types';
import { useMutation } from '@apollo/client/react';
import { ACKNOWLEDGE_ALERT, RESOLVE_ALERT, GET_ACTIVE_ALERTS } from '../graphql/queries';

interface AlertDetailDrawerProps {
  alert: Alert | null;
  onClose: () => void;
}

export default function AlertDetailDrawer({ alert, onClose }: AlertDetailDrawerProps) {
  const [acknowledge] = useMutation(ACKNOWLEDGE_ALERT, {
    refetchQueries: [{ query: GET_ACTIVE_ALERTS }],
  });
  const [resolve] = useMutation(RESOLVE_ALERT, {
    refetchQueries: [{ query: GET_ACTIVE_ALERTS }],
  });

  if (!alert) return null;

  return (
    <div className="fixed inset-y-0 right-0 w-96 bg-gray-900 border-l border-gray-700 shadow-2xl overflow-y-auto" style={{ zIndex: 1000 }}>
      <div className="p-6">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-white text-lg font-bold">Alert Details</h2>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-white text-2xl leading-none"
          >
            &times;
          </button>
        </div>

        <div className="space-y-4">
          <div>
            <span className={`px-2 py-1 rounded text-xs font-bold ${severityColor(alert.severity)}`}>
              {alert.severity}
            </span>
            <span className={`ml-2 px-2 py-1 rounded text-xs ${statusColor(alert.status)}`}>
              {alert.status}
            </span>
          </div>

          <h3 className="text-white text-xl font-semibold">{alert.title}</h3>

          {alert.description && (
            <p className="text-gray-300 text-sm">{alert.description}</p>
          )}

          <div className="bg-gray-800 rounded-lg p-4">
            <h4 className="text-gray-400 text-xs font-semibold uppercase mb-2">Location</h4>
            <p className="text-white text-sm">{alert.location.zone || 'Unknown'}</p>
            <p className="text-gray-500 text-xs">
              {alert.location.lat.toFixed(4)}, {alert.location.lng.toFixed(4)}
            </p>
            {(alert.location.affectedBuildings?.length ?? 0) > 0 && (
              <div className="mt-2">
                <p className="text-gray-400 text-xs mb-1">Affected Buildings:</p>
                <div className="flex flex-wrap gap-1">
                  {alert.location.affectedBuildings.map((b) => (
                    <span key={b} className="bg-gray-700 text-gray-300 text-xs px-2 py-0.5 rounded">
                      {b}
                    </span>
                  ))}
                </div>
              </div>
            )}
          </div>

          {alert.deliveryStats && (
            <div className="bg-gray-800 rounded-lg p-4">
              <h4 className="text-gray-400 text-xs font-semibold uppercase mb-3">
                Delivery Stats
              </h4>
              <div className="grid grid-cols-3 gap-3 mb-3">
                <div className="text-center">
                  <p className="text-2xl font-bold text-white">
                    {alert.deliveryStats.totalRecipients}
                  </p>
                  <p className="text-gray-500 text-xs">Total Sent</p>
                </div>
                <div className="text-center">
                  <p className="text-2xl font-bold text-green-400">
                    {alert.deliveryStats.delivered}
                  </p>
                  <p className="text-gray-500 text-xs">Delivered</p>
                </div>
                <div className="text-center">
                  <p className="text-2xl font-bold text-red-400">
                    {alert.deliveryStats.failed}
                  </p>
                  <p className="text-gray-500 text-xs">Failed</p>
                </div>
              </div>
              <p className="text-gray-400 text-xs text-center">
                Avg latency: {alert.deliveryStats.avgLatencyMs}ms
              </p>

              {(alert.deliveryStats.byChannel?.length ?? 0) > 0 && (
                <div className="mt-3 space-y-2">
                  {alert.deliveryStats.byChannel.map((ch) => (
                    <div key={ch.channel} className="flex items-center justify-between text-xs">
                      <span className="text-gray-300 uppercase font-medium w-12">
                        {ch.channel}
                      </span>
                      <div className="flex-1 mx-2 bg-gray-700 rounded-full h-2">
                        <div
                          className="bg-green-500 h-2 rounded-full"
                          style={{ width: `${ch.sent > 0 ? (ch.delivered / ch.sent) * 100 : 0}%` }}
                        />
                      </div>
                      <span className="text-gray-400 w-16 text-right">
                        {ch.delivered}/{ch.sent}
                      </span>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}

          <div className="text-gray-500 text-xs">
            Created: {new Date(alert.createdAt).toLocaleString()}
          </div>

          {alert.status === 'ACTIVE' && (
            <div className="flex gap-2 pt-2">
              <button
                onClick={() => acknowledge({ variables: { id: alert.id } })}
                className="flex-1 bg-yellow-600 hover:bg-yellow-700 text-white py-2 px-4 rounded-lg text-sm font-medium transition-colors"
              >
                Acknowledge
              </button>
              <button
                onClick={() => resolve({ variables: { id: alert.id } })}
                className="flex-1 bg-green-600 hover:bg-green-700 text-white py-2 px-4 rounded-lg text-sm font-medium transition-colors"
              >
                Resolve
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

function severityColor(severity: string) {
  const colors: Record<string, string> = {
    CRITICAL: 'bg-red-600 text-white',
    HIGH: 'bg-orange-500 text-white',
    MEDIUM: 'bg-yellow-500 text-black',
    LOW: 'bg-blue-500 text-white',
  };
  return colors[severity] || 'bg-gray-500 text-white';
}

function statusColor(status: string) {
  const colors: Record<string, string> = {
    ACTIVE: 'bg-red-800 text-red-200',
    ACKNOWLEDGED: 'bg-yellow-800 text-yellow-200',
    RESOLVED: 'bg-green-800 text-green-200',
  };
  return colors[status] || 'bg-gray-700 text-gray-300';
}
