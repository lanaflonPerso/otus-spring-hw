import React, {useEffect, useState} from 'react';
import {Link} from 'react-router-dom';

import PageTitle from './layout/PageTitle';

import Loading from './Loading';
import {getFromApi} from "../utils/backendApi";

export default function Authors() {
    const [authors, setAuthors] = useState([]);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        getFromApi("/api/v1/authors")
            .then(authors => setAuthors(authors))
            .finally(() => setIsLoading(false));
    }, []);

    return (
        <>
            <PageTitle title="Authors"/>
            {isLoading ?
                <Loading/>
                :
                authors.length > 0 ?
                    <>
                        <table className="table table-hover w-50">
                            <thead>
                            <tr>
                                <th>Name</th>
                                <th>Homeland</th>
                            </tr>
                            </thead>
                            <tbody>
                            {
                                authors.map((author, i) => (
                                    <tr key={i}>
                                        <td>
                                            <Link
                                                to={`/authors/${author.id}`}>{author.firstname} {author.lastname}</Link>
                                        </td>
                                        <td>{author.homeland}</td>
                                    </tr>
                                ))
                            }
                            </tbody>
                        </table>
                    </>
                    :
                    <p>No data available</p>
            }
            <Link className="btn btn-success" to="/authors/new">Create new author</Link>
        </>
    )
}