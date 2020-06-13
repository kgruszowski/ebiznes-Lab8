import React, {Component} from "react";
import {Link} from "react-router-dom";

class DiscountDetails extends Component {
    constructor(props) {
        super(props);
        this.state = {
            discount: [],
            discountId: props.match.params.id,
        }
    }

    componentDidMount() {
        let url = "http://localhost:9000/api/discount/" + this.state.discountId;

        fetch(url, {
            mode: 'cors',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Access-Control-Allow-Origin': 'http://localhost:3000',
            },
            method: 'GET',
        })
            .then(results => {
                return results.json();
            }).then(data => {
            let discountDetails = (
                <div>
                    <div className="col-md-7">
                        <div>Id: {data.discount.id}</div>
                        <div>Code: {data.discount.code}</div>
                        <div>Value: {data.discount.value}%</div>
                    </div>
                    <hr></hr>
                    <div className="btn-group">
                        <Link to={"/discount/edit/" + this.state.discountId}
                              className="btn btn-sm btn-success">Edit</Link>
                    </div>
                </div>
            )
            this.setState({discount: discountDetails})
        })
    }

    render() {
        return (
            <div className="item-container">
                <div className="container">
                    {this.state.discount}
                </div>
            </div>

        )
    }
}

export default DiscountDetails;