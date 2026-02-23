import { Link, useLocation } from 'react-router-dom';

export default function Navbar() {
  const location = useLocation();

  const linkClass = (path: string) =>
    `px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
      location.pathname === path
        ? 'bg-red-600 text-white'
        : 'text-gray-300 hover:bg-gray-700 hover:text-white'
    }`;

  return (
    <nav className="bg-gray-900 border-b border-gray-800">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          <div className="flex items-center gap-2">
            <span className="text-red-500 text-2xl">&#9888;</span>
            <span className="text-white font-bold text-lg">Campus Safety</span>
          </div>
          <div className="flex gap-2">
            <Link to="/" className={linkClass('/')}>
              Dashboard
            </Link>
            <Link to="/admin" className={linkClass('/admin')}>
              Admin Panel
            </Link>
            <Link to="/history" className={linkClass('/history')}>
              History
            </Link>
          </div>
        </div>
      </div>
    </nav>
  );
}
