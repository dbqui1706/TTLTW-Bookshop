// STATIC DATA
const contextPathMetaTag = document.querySelector("meta[name='contextPath']");
const currentUserIdMetaTag = document.querySelector("meta[name='currentUserId']");

// ROOTS/ELEMENTS
const totalCartItemsQuantityRootElement = document.querySelector("#total-cart-items-quantity");
const orderDangerBadgeElement = document.querySelector("#order-danger");

// UTILS
async function _fetchGetCart() {
  const response = await fetch(contextPathMetaTag.content + "/cartItem?userId=" + currentUserIdMetaTag.content, {
    method: "GET",
    headers: {
      "Accept": "application/json",
      "Content-Type": "application/json",
    },
  });
  return [response.status, await response.json()];
}

async function _fetchGetOrderDanger() {
    const response = await fetch(contextPathMetaTag.content + "/orderDangerCheck", {
       method: "GET",
       headers: {
         "Accept": "application/json",
         "Content-Type": "application/json",
       },
     });
    return [response.status, await response.json()];
}

// STATE
const state = {
  totalCartItemsQuantity: 0,
  setTotalCartItemsQuantity: (value) => {
    if (typeof value === "string") {
      state.totalCartItemsQuantity += Number(value);
    } else {
      state.totalCartItemsQuantity = value.cartItems
        .map((cartItem) => cartItem.quantity)
        .reduce((partialSum, cartItemQuantity) => partialSum + cartItemQuantity, 0);
    }
    render();
  },
  orderDangerStatus: "FINE",
  setOrderDangerStatus: (value) => {
    state.orderDangerStatus = value.status;
    render();
  },
  initState: async () => {
    const [status, data] = await _fetchGetCart();
    if (status === 200) {
      state.setTotalCartItemsQuantity(data);
    }
    const [status2, data2] = await _fetchGetOrderDanger();
    if (status2 === 200) {
      state.setOrderDangerStatus(data2);
    }
  },
}

// RENDER
function render() {
  totalCartItemsQuantityRootElement.innerHTML = state.totalCartItemsQuantity;
  if(state.orderDangerStatus === "FINE"){
    orderDangerBadgeElement.classList.add("invisible");
  }
  else {
    orderDangerBadgeElement.classList.remove("invisible");
  }
}

// MAIN
if (currentUserIdMetaTag) {
  void state.initState();
}

export const setTotalCartItemsQuantity = state.setTotalCartItemsQuantity;
