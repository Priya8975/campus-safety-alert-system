import { useState } from 'react';
import { useMutation } from '@apollo/client/react';
import { CREATE_ALERT, GET_ACTIVE_ALERTS } from '../graphql/queries';
import type { Severity } from '../types';

interface CreateAlertFormProps {
  lat: number | null;
  lng: number | null;
}

export default function CreateAlertForm({ lat, lng }: CreateAlertFormProps) {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [severity, setSeverity] = useState<Severity>('HIGH');
  const [campusZone, setCampusZone] = useState('');
  const [success, setSuccess] = useState(false);

  const [createAlert, { loading, error }] = useMutation(CREATE_ALERT, {
    refetchQueries: [{ query: GET_ACTIVE_ALERTS }],
    onCompleted: () => {
      setTitle('');
      setDescription('');
      setCampusZone('');
      setSuccess(true);
      setTimeout(() => setSuccess(false), 3000);
    },
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!lat || !lng) return;

    createAlert({
      variables: {
        input: {
          title,
          description: description || undefined,
          severity,
          lat,
          lng,
          campusZone: campusZone || undefined,
        },
      },
    });
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <label className="block text-gray-300 text-sm font-medium mb-1">Title *</label>
        <input
          type="text"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          required
          placeholder="e.g., Suspicious activity near SAC"
          className="w-full bg-gray-700 text-white rounded-lg px-3 py-2 text-sm border border-gray-600 focus:border-red-500 focus:outline-none"
        />
      </div>

      <div>
        <label className="block text-gray-300 text-sm font-medium mb-1">Description</label>
        <textarea
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          rows={3}
          placeholder="Provide details about the incident..."
          className="w-full bg-gray-700 text-white rounded-lg px-3 py-2 text-sm border border-gray-600 focus:border-red-500 focus:outline-none resize-none"
        />
      </div>

      <div>
        <label className="block text-gray-300 text-sm font-medium mb-1">Severity *</label>
        <div className="grid grid-cols-4 gap-2">
          {(['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'] as Severity[]).map((s) => (
            <button
              key={s}
              type="button"
              onClick={() => setSeverity(s)}
              className={`py-2 px-3 rounded-lg text-xs font-bold transition-colors ${
                severity === s ? severityActive(s) : 'bg-gray-700 text-gray-400 hover:bg-gray-600'
              }`}
            >
              {s}
            </button>
          ))}
        </div>
      </div>

      <div>
        <label className="block text-gray-300 text-sm font-medium mb-1">Campus Zone</label>
        <input
          type="text"
          value={campusZone}
          onChange={(e) => setCampusZone(e.target.value)}
          placeholder="e.g., SAC, Engineering"
          className="w-full bg-gray-700 text-white rounded-lg px-3 py-2 text-sm border border-gray-600 focus:border-red-500 focus:outline-none"
        />
      </div>

      <div className="bg-gray-700 rounded-lg p-3">
        <label className="block text-gray-400 text-xs font-medium mb-1">Location (click map to set)</label>
        {lat && lng ? (
          <p className="text-white text-sm">
            {lat.toFixed(6)}, {lng.toFixed(6)}
          </p>
        ) : (
          <p className="text-yellow-400 text-sm">Click on the map to pin the incident location</p>
        )}
      </div>

      {error && (
        <p className="text-red-400 text-sm">Error: {error.message}</p>
      )}

      {success && (
        <p className="text-green-400 text-sm">Alert created successfully!</p>
      )}

      <button
        type="submit"
        disabled={loading || !title || !lat || !lng}
        className="w-full bg-red-600 hover:bg-red-700 disabled:bg-gray-600 disabled:cursor-not-allowed text-white py-3 rounded-lg font-semibold transition-colors"
      >
        {loading ? 'Creating...' : 'Create Alert'}
      </button>
    </form>
  );
}

function severityActive(s: string): string {
  const map: Record<string, string> = {
    CRITICAL: 'bg-red-600 text-white',
    HIGH: 'bg-orange-500 text-white',
    MEDIUM: 'bg-yellow-500 text-black',
    LOW: 'bg-blue-500 text-white',
  };
  return map[s] || '';
}
