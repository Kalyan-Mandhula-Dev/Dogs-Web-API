import { useEffect, useState } from "react";
import { useLocation, useParams } from "react-router"
import '../../styles/breeds.css'
import EditSubBreedForm from "./forms//EditSubBreedForm";
import DeleteSubBreedPopup from "./actions//DeleteSubBreedPopup";
import AddSubBreedForm from "./forms/AddSubBreedForm";
import { useNavigate } from "react-router";



export default function SubBreeds() {

    const { breedId } = useParams();
    const location = useLocation();
    const breedName = location.state?.breedName;
    const [subBreeds, setSubBreeds] = useState([])
    const [selectedSubBreed, setSelectedSubBreed] = useState(null);
    const [showEditPopUp, setShowEditPopUp] = useState(false);
    const [showConfirmDeletePopUp, setShowConfirmDeletePopUp] = useState(false);
    const [showAddPopUp, setShowAddPopUp] = useState(false);
    const [lastId, setLastId] = useState(0);
    const [hasMore, setHasMore] = useState(false);
    const [loading, setLoading] = useState(true);

    const SUBBREEDS_API_URL = import.meta.env.VITE_DOGS_API_URL + `/v1/breeds/${breedId}/subbreeds`;
    const navigate = useNavigate();

    const fetchSubBreeds = async () => {

        console.log(SUBBREEDS_API_URL, lastId);

        const response = await fetch(
            SUBBREEDS_API_URL + `?lastId=${lastId}`
        );

        const data = await response.json();
        console.log("sub-breeds : ", data)

        setSubBreeds(prev => [...prev, ...data]);
        setLoading(false);

        if (data.length > 0) {
            setLastId(data[data.length - 1].id);
        }

        setHasMore(data.length === 10);
    };

    useEffect(() => {
        fetchSubBreeds();
    }, [])

    return (<>
        <div className="breed-page">
            <button className="back-btn" onClick={() => navigate("/")}>
                ← <span>Back</span>
            </button>
            <div className='header-div'>
                <h2>{breedName} Sub-breeds 🐶</h2>
                <div className='header-actions'>
                    <button onClick={() => setShowAddPopUp(true)}>Add sub-breed</button>
                </div>
            </div>

            <table className="breed-table">
                <tr>
                    <th>Name</th>
                    <th>Last modified at</th>
                    <th>Action</th>
                </tr>
                {
                    loading ? (
                        <tr>
                            <td colSpan="4" className="no-breeds-container">
                                Loading sub-breeds...
                            </td>
                        </tr>
                    ) : subBreeds.length !== 0 ? subBreeds.map((subBreed, index) => {
                        return (
                            <tr
                                key={index}
                            >
                                <td>{subBreed.name}</td>
                                <td>
                                    {new Date(subBreed.modifiedAt).toLocaleString('en-US', {
                                        timeZone: 'Asia/Kolkata',
                                        month: 'short',
                                        day: 'numeric',
                                        hour: 'numeric',
                                        minute: '2-digit'
                                    })}
                                </td>
                                <td>
                                    <button className="edit-button"
                                        title="edit sub-breed"
                                        onClick={() => {
                                            setSelectedSubBreed(subBreed);
                                            setShowEditPopUp(true);
                                        }}>✏️</button>
                                    <button className="delete-button"
                                        title="delete sub-breed"
                                        onClick={() => {
                                            setSelectedSubBreed(subBreed);
                                            setShowConfirmDeletePopUp(true);
                                        }}>🗑️</button>
                                </td>
                            </tr>
                        );
                    }) : <tr>
                        <td colSpan='4' className='no-breeds-container'>
                            No sub-breeds found
                        </td></tr>
                }
            </table>

            {hasMore && (<div className='cursor-page-load-more'>
                <button onClick={fetchSubBreeds}>
                    Load More
                </button></div>
            )}

            {
                showEditPopUp && selectedSubBreed && <EditSubBreedForm SUBBREEDS_API_URL={SUBBREEDS_API_URL} setShowEditPopUp={setShowEditPopUp} selectedSubBreed={selectedSubBreed} setSubBreeds={setSubBreeds} />
            }

            {
                showConfirmDeletePopUp && selectedSubBreed && <DeleteSubBreedPopup SUBBREEDS_API_URL={SUBBREEDS_API_URL} setShowConfirmDeletePopUp={setShowConfirmDeletePopUp} selectedSubBreed={selectedSubBreed} setSubBreeds={setSubBreeds} />
            }

            {
                showAddPopUp && <AddSubBreedForm SUBBREEDS_API_URL={SUBBREEDS_API_URL} hasMore={hasMore} setShowAddPopUp={setShowAddPopUp} subBreeds={subBreeds} setSubBreeds={setSubBreeds} />
            }
        </div>
    </>)
}