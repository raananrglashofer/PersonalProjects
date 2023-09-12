import json
import requests
from twilio.rest import Client
import flask
import time
import datetime

# creating sets to keep track of called stocks and coins
coins = set()
stocks = set()

def get_stock_price(ticker):
    ticker = ticker.upper()
    url = "https://www.alphavantage.co/query"
    api_key = "API Key"

    symbol = ticker
    interval = "1min"

    params = {
        "function": "TIME_SERIES_INTRADAY",
        "symbol": symbol,
        "interval": interval,
        "apikey": api_key,
    }
    response = requests.get(url, params=params)
    data = response.json()
    latest_price = list(data["Time Series ({})".format(interval)].values())[0]["4. close"]
    return float(latest_price)

def get_crypto_price(ticker):
    ticker = ticker.upper()
    API_KEY = "API Key"
    URL = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest"
    headers = {
    "Accepts": "application/json",
    "X-CMC_PRO_API_KEY": "API Key"
    }

    parameters = {
    "symbol": ticker,
    "convert": "USD"
    }
    response = requests.get(URL, headers=headers, params=parameters)
    data = json.loads(response.text)
    coin_price = data["data"][ticker]["quote"]["USD"]["price"]
    return coin_price

def add_to_map(ticker):
    return
    # going to have a set of all the tickers called
    # for each ticker it will be connected to a hashmap of time -> price
    # the hashmap will only consist of the ten latest calls.
    # i.e. if you call ETH for the 11th time, then the 1st entry is written over
    # the idea here is to have running averages from the last ten calls if the user wants that

def buy_or_sell(choice, ticker, sell_price, buy_price):
    if ticker is None or sell_price is None or buy_price is None:
        raise ValueError("Must be a valid ticker and valid buy and sell prices")
    if buy_price >= sell_price:
        raise ValueError("Sell price must be greater than buy price")

    if choice == 1:
        price = get_crypto_price(ticker)
        coins.add(ticker.upper())
    elif choice == 2:
        price = get_stock_price(ticker)
        stocks.add(ticker.upper())
    else:
        ValueError("Invalid Choice")

    if price > sell_price:
        print("Current", ticker, "Price in USD: $", round(price, 2), "\nNow is a good time to sell")
        text = "Current " + ticker + " Price in USD: $" + str(round(price, 2)) + "\n Now is a good time to sell"
        return text
    if price < buy_price:
        print("Current", ticker, "Price in USD: $", round(price, 2), "\nNow is a good time to buy")
        text = "Current " + ticker + " Price in USD: $" + str(round(price, 2)) + "\n Now is a good time to buy"
        return text
    else:
        print("Current", ticker, "Price in USD: $", round(price, 2), "\nNow is a good time to hold")
        text = "Current " + ticker + " Price in USD: $" + str(round(price, 2))+ "\n Now is a good time to hold"
        return text

def send_text(phone_number, str):
    account_sid = "account sid"
    auth_token = "auth token"
    client = Client(account_sid, auth_token)
    from_phone_number = "Phone Number"
    client.messages.create(
        body = str,
        from_= from_phone_number,
        to = phone_number
    )
if __name__ == '__main__':
     #buy_or_sell(1, "ETH", 2, 1)
    # buy_or_sell(2, "AAPL", 200, 150)
     buy_or_sell(2, "EVLV", 8, 5.5)
     #buy_or_sell(2, "MAR", 200, 150)
     #send_text(+12402748746, buy_or_sell(2, "MAR", 200, 150))
  #  now = datetime.datetime.now()
   # end_of_day = datetime.datetime.combine(now.date(), datetime.time(23, 59, 59))
   # while now < end_of_day:
        #send_text(+12408555109, buy_or_sell(1, "ETH", 2000, 1400))
     #   time.sleep(3600)
      #  now = datetime.datetime.now()