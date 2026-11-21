
const API_URL = import.meta.env.VITE_DOGS_API_URL;

class ApiService {

    static async request(endpoint, options = {}) {
        try {

            const response = await fetch(`${API_URL}${endpoint}`, options);

            let data = null;

            try {
                data = await response.json();
            } catch {
                // No response body
            }

            if (!response.ok) {
                return {
                    status: response.status,
                    success: false,
                    message: data?.message ?? `Request failed ${response.status}`
                }
            }

            return {
                status: response.status,
                success: true,
                data
            }

        } catch (error) {
            return {
                success: false,
                status: null,
                message: "Unable to connect to the server. Please try again later."
            }
        }
    }


    static get(endpoint) {
        return this.request(endpoint);
    }

    static post(endpoint, body) {

        const isFormData = body instanceof FormData;

        return this.request(endpoint, {
            method: "POST",
            ...(isFormData ? {} : {
                headers: {
                    "Content-Type": "application/json"
                }
            }),
            body: isFormData ? body : JSON.stringify(body)
        });
    }

    static put(endpoint, body) {
        return this.request(endpoint, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(body)
        });
    }

    static delete(endpoint) {
        return this.request(endpoint, {
            method: "DELETE"
        });
    }
}

export default ApiService;