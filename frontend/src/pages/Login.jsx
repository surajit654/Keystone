import { useNavigate } from "react-router-dom";

function Login() {
  const navigate = useNavigate();

  const handleLogin = (e) => {
    e.preventDefault();

    // For now, just navigate to dashboard
    navigate("/dashboard");
  };

  return (
    <div className="login-page">
      <h1>KEYSTONE Login</h1>

      <form onSubmit={handleLogin}>
        <div>
          <label>Email</label>
          <input
            type="email"
            placeholder="Enter email"
          />
        </div>

        <div>
          <label>Password</label>
          <input
            type="password"
            placeholder="Enter password"
          />
        </div>

        <button type="submit">Login</button>
      </form>
    </div>
  );
}

export default Login;