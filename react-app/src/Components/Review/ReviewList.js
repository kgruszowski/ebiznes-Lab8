import React, {Component} from "react";
import {Link} from "react-router-dom";

class ReviewList extends Component {
    constructor(props) {
        super(props);
        this.state = {
            productId: props.productId,
            reviews: ""
        }
    }

    componentDidMount() {
        let url = "http://localhost:9000/api/reviews/product/" + this.state.productId;

        fetch(url, {
            mode: 'cors',
            headers: {},
            method: 'GET',
        })
            .then(results => {
                return results.json();
            }).then(data => {
            let reviews = data.map((review) => {
                return (
                    <li key={review.id}>
                        {review.rate}/5<br/>
                        {review.comment}<br/>
                        <Link to={"/review/edit/" + review.id}>Edit</Link>
                        <hr/>
                    </li>
                )
            })
            this.setState({reviews: reviews})
        })
    }

    render() {
        return (
            <ul>
                {this.state.reviews}
            </ul>
        );
    }
}

export default ReviewList;
