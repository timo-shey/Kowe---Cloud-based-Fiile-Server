import React, { useState, useEffect } from "react";
import "./BudgetCreatedDash.css";
import { Calendar } from "react-calendar";
import LineItemModal from "../../modals/LineItemModals";
import axios from "axios";
import {baseEndpoint} from "../../../globalresources/Config";

function BudgetCreatedDash() {
  const token = localStorage.getItem("token");

  const [value, onChange] = useState(new Date());
  const [itemModal, setItemModal] = useState(false);

  const [budgetItem, setBudgetItem] = useState({});
  const [budgetLineItemList, setBudgetLineItemList] = useState([]);

  useEffect(() => {
    if (token !== null) {
      getBudgetItem();
    }
  }, []);

  const getBudgetItem = async () => {
    try {
      const response = await axios.get(baseEndpoint+"/api/v1/budgets", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      const item = response.data.reverse()[0];
      setBudgetItem(item);
      setBudgetLineItemList(item.lineItemRests);
    } catch (error) {
      console.log(error.message);
    }
  };

  const createBudgetHandler = () => {
    setItemModal(true);
  };

  return (
    <main>
      <div className="dashboard-decapay-qhb">
        <img className="ellipse-4-9iH" src="/assets/ellipse-4-c4h.png" />
        <div className="frame-8768-seH">
          <div className="frame-8770-mzZ">
            <div className="auto-group-e345-jAh">
              <div className="frame-8767-Fuj">
                <div className="frame-8766-Cpy">
                  <div className="frame-8757-m7P">
                    <div className="frame-8756-uUV">
                      <div className="rectangle-2-GK3"></div>
                      <div className="frame-8625-Qg9">
                        <div className="my-budget-Y1f">My Budget</div>
                        <div className="n3000000-TPX">N{budgetItem.amount}</div>
                      </div>
                      <img
                        className="ellipse-3-BaR"
                        src="/assets/ellipse-3-5YD.png"
                      />
                    </div>
                  </div>
                  <div className="frame-8765-Wch">
                    <div className="frame-8761-rAm">
                      <div className="frame-8760-acZ">
                        <div className="frame-8758-Y3b">
                          <div className="total-amount-spent-Hms">
                            Total Amount
                            <br />
                            spent
                          </div>
                          <div className="n20000-1Sy">
                            N{budgetItem.totalAmountSpent}
                          </div>
                        </div>
                        <img
                          className="frame-8759-wrR"
                          src="/assets/frame-8759-5kM.png"
                        />
                      </div>
                    </div>
                    <div className="frame-8764-Tpm">
                      <div className="frame-8763-1LV">
                        <div className="percentage-xmX">Percentage</div>
                        <div className="item-375-Hoo">
                          {budgetItem.percentage}%
                        </div>
                      </div>
                      <img
                        className="frame-8762-2Fb"
                        src="/assets/frame-8762-fWh.png"
                      />
                    </div>
                  </div>
                </div>
                <div className="calendar-2022-839-2022-month-05-may-LXB">
                  <div className="header-fJZ">
                    {/* <div className="calendar-2022-839-atoms-head-mcV">May</div> */}
                  </div>
                  <div>
                    <Calendar onChange={onChange} value={value} />
                    <h5>{value.toString()}</h5>
                  </div>
                </div>
              </div>
              {budgetLineItemList.map((item) => (
                <div key={item.lineItemId} className="frame-8771-Kww">
                  <div className="frame-8747-sTf">
                    <div className="frame-8745-nad">
                      <div className="groceries-KaZ">{item.category}</div>
                      <div className="projected-amount-n5500-TRs">
                        <span className="projected-amount-n5500-TRs-sub-0">
                          Projected amount -{" "}
                        </span>
                        <span className="projected-amount-n5500-TRs-sub-1">
                          N{item.projectedAmount}
                        </span>
                      </div>
                      <div className="frame-8751-Y5w">
                        <div className="amount-so-far-n2500-hUd">
                          <span className="amount-so-far-n2500-hUd-sub-0">
                            Amount so far -{" "}
                          </span>
                          <span className="amount-so-far-n2500-hUd-sub-1">
                            N{item.amountSpentSoFar}
                          </span>
                        </div>
                        <div className="view-expenses-fZs">View expenses</div>
                      </div>
                    </div>
                    <div className="frame-8746-nPb">
                      <div className="frame-8639-XMB">
                        <div className="log-ewb">Log</div>
                        <img
                          className="arrow-up-right-xSV"
                          src="./assets/arrow-up-right-PXP.png"
                        />
                      </div>
                      <div className="item-375-s3f">
                        {item.percentageSpentSoFar}%
                      </div>
                    </div>
                  </div>
                  {/* <div className="frame-8771-MjX">
                  <div className="frame-8745-Up9">
                    <div className="transportation-1p5">Transportation</div>
                    <div className="projected-amount-n5500-wxd">
                      <span className="projected-amount-n5500-wxd-sub-0">
                        Projected amount -{" "}
                      </span>
                      <span className="projected-amount-n5500-wxd-sub-1">
                        N5,500
                      </span>
                    </div>
                    <div className="frame-8751-v3s">
                      <div className="amount-so-far-n2500-GtR">
                        <span className="amount-so-far-n2500-GtR-sub-0">
                          Amount so far -{" "}
                        </span>
                        <span className="amount-so-far-n2500-GtR-sub-1">
                          N2,500
                        </span>
                      </div>
                      <div className="view-expenses-3nh">View expenses</div>
                    </div>
                  </div>
                  <div className="frame-8746-Na5">
                    <div className="frame-8639-vLh">
                      <div className="log-TLd">Log</div>
                      <img
                        className="arrow-up-right-oQV"
                        src="./assets/arrow-up-right-LPT.png"
                      />
                    </div>
                    <div className="item-375-XLV">3.75%</div>
                  </div>
                </div> */}
                  {/* <div className="frame-8770-DUD">
                  <div className="frame-8745-kDF">
                    <div className="clothing-gch">Clothing</div>
                    <div className="projected-amount-n5500-E8R">
                      <span className="projected-amount-n5500-E8R-sub-0">
                        Projected amount -{" "}
                      </span>
                      <span className="projected-amount-n5500-E8R-sub-1">
                        N5,500
                      </span>
                    </div>
                    <div className="frame-8751-Bxm">
                      <div className="amount-so-far-n2500-MMT">
                        <span className="amount-so-far-n2500-MMT-sub-0">
                          Amount so far -{" "}
                        </span>
                        <span className="amount-so-far-n2500-MMT-sub-1">
                          N2,500
                        </span>
                      </div>
                      <div className="view-expenses-8mX">View expenses</div>
                    </div>
                  </div>
                  <div className="frame-8746-sUD">
                    <div className="frame-8639-1aR">
                      <div className="log-MPP">Log</div>
                      <img
                        className="arrow-up-right-gwT"
                        src="./assets/arrow-up-right-bow.png"
                      />
                    </div>
                    <div className="item-375-DgV">3.75%</div>
                  </div>
                </div> */}
                </div>
              ))}
            </div>
            <div className="frame-8754-Jhw">
              <img className="plus-dVK" src="./assets/plus-fJR.png" />
              <div className="create-budget-B13" onClick={createBudgetHandler}>
                Create Budget Line Item
              </div>
            </div>
          </div>
        </div>
      </div>

      <div>
        {itemModal && (
          <LineItemModal
            itemModal={itemModal}
            setItemModal={setItemModal}
            budgetId={budgetItem.budgetId}
          />
        )}
      </div>
    </main>
  );
}

export default BudgetCreatedDash;
