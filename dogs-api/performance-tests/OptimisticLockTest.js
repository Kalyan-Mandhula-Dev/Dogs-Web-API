
import http from 'k6/http';
import { check } from 'k6';

const BASE_URL = 'http://localhost:8080/api/v1/breeds';

export const options = {
    vus: 2,
    iterations: 2
};

export default function () {

    const BODY = JSON.stringify({
        name: 'Labrador-2',
        version: 0
    });

    const PARAMS = {
        headers: {
            'Content-Type': 'application/json'
        }
    };

    const response = http.put(BASE_URL + '/27', BODY, PARAMS);

    check(response, {
        'Status is 200': (res) => res.status === 200,
        'Status is 409': (res) => res.status === 409
    });

}
