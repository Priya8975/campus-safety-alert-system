import type { Alert } from '../types';

const severityColors: Record<string, string> = {
  CRITICAL: 'bg-red-600 text-white',
  HIGH: 'bg-orange-500 text-white',
  MEDIUM: 'bg-yellow-500 text-black',
  LOW: 'bg-blue-500 text-white',
};

const severityBorders: Record<string, string> = {
  CRITICAL: 'border-red-600',
  HIGH: 'border-orange-500',
  MEDIUM: 'border-yellow-500',
  LOW: 'border-blue-500',
};

export interface AlertCardProps {
  alert: Alert;
  onClick?: () => void;
  isSelected?: boolean;
}

export default function AlertCard({ alert, onClick, isSelected }: AlertCardProps) {
  const timeAgo = getTimeAgo(alert.createdAt);

  return (
    <div
      className={`bg-gray-800 rounded-lg border-l-4 ${severityBorders[alert.severity]} p-4 cursor-pointer hover:bg-gray-750 transition-colors ${isSelected ? 'ring-2 ring-red-500' : ''}`}
      onClick={onClick}
    >
      <div className="flex items-start justify-between mb-2">
        <h3 className="text-white font-semibold text-sm leading-tight flex-1 mr-2">
          {alert.title}
        </h3>
        <span
          className={`px-2 py-0.5 rounded text-xs font-bold ${severityColors[alert.severity]}`}
        >
          {alert.severity}
        </span>
      </div>

      {alert.description && (
        <p className="text-gray-400 text-xs mb-2 line-clamp-2">{alert.description}</p>
      )}

      <div className="flex items-center justify-between text-xs text-gray-500">
        <span>{alert.location.zone || 'Unknown zone'}</span>
        <span>{timeAgo}</span>
      </div>

      {alert.status !== 'ACTIVE' && (
        <span
          className={`inline-block mt-2 px-2 py-0.5 rounded text-xs ${
            alert.status === 'RESOLVED'
              ? 'bg-green-800 text-green-200'
              : 'bg-yellow-800 text-yellow-200'
          }`}
        >
          {alert.status}
        </span>
      )}

      {alert.deliveryStats && (
        <div className="mt-2 pt-2 border-t border-gray-700">
          <div className="flex gap-3 text-xs">
            <span className="text-green-400">
              {alert.deliveryStats.delivered} delivered
            </span>
            <span className="text-red-400">
              {alert.deliveryStats.failed} failed
            </span>
            <span className="text-gray-400">
              {alert.deliveryStats.avgLatencyMs}ms avg
            </span>
          </div>
        </div>
      )}
    </div>
  );
}

function getTimeAgo(dateStr: string): string {
  const now = new Date();
  const date = new Date(dateStr);
  const seconds = Math.floor((now.getTime() - date.getTime()) / 1000);

  if (seconds < 60) return 'just now';
  if (seconds < 3600) return `${Math.floor(seconds / 60)}m ago`;
  if (seconds < 86400) return `${Math.floor(seconds / 3600)}h ago`;
  return `${Math.floor(seconds / 86400)}d ago`;
}
