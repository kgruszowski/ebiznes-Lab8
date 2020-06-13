import React, {Component} from "react";
import {Redirect} from "react-router-dom";
import InputComponent from "../Common/InputComponent";

class ReviewAdd extends Component {
    constructor(props) {
        super(props);
        this.state = {
            id: 0,
            productId: props.match.params.productId,
            rate: 5,
            comment: "",
            reviewCreated: false
        }
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleInputChange = this.handleInputChange.bind(this);
    }

    handleSubmit(event) {
        var data = {
            "id": this.state.id,
            "product": parseInt(this.state.productId),
            "rate": parseInt(this.state.rate),
            "comment": this.state.comment
        };
        var url = 'http://localhost:9000/api/review';

        fetch(url, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data),
        }).then(results => {
            return results.json();
        }).then(responseData => {
            this.setState({
                reviewCreated: true,
                id: responseData.review.id
            })
        });
    }

    handleInputChange(name, value) {
        this.setState({
            [name]: value
        });
    }

    render() {
        if (this.state.reviewCreated) {
            return <Redirect to={"/product/" + this.state.productId}/>
        }
        return (
            <div>
                <InputComponent label="Rate" name="rate" value={this.state.rate}
                                onChangeCallback={this.handleInputChange}/>

                <InputComponent label="Comment" name="comment" value={this.state.comment}
                                onChangeCallback={this.handleInputChange}/>

                <button className="btn btn-sm btn-success" onClick={this.handleSubmit}>Create review</button>
            </div>
        );
    }
}

export default ReviewAdd;