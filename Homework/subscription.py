import datetime
import json


class Subscription:
    def __init__(self, company: list = None, value: list = None, drop: list = None, variation: list = None, date: list = None):
        self.company = company
        self.value = value
        self.drop = drop
        self.variation = variation
        self.date = date

    def to_json(self):
        # create a json object from the object with the attributes that are not None
        # return the json object
        json_dict = {}
        if self.company:
            json_dict['company'] = self.company

        if self.value:
            json_dict['value'] = self.value

        if self.drop:
            json_dict['drop'] = self.drop

        if self.variation:
            json_dict['variation'] = self.variation

        if self.date:
            json_dict['date'] = self.date #.strftime('%d-%m-%Y')

        return json_dict
    
    @staticmethod
    def from_json(json_dict):
        # create a new object from the json object
        # return the new object
        #json_dict = json.loads(json_string)
        return Subscription(
            company=json_dict.get("company"),
            value=json_dict.get("value"),
            drop=json_dict.get("drop"),
            variation=json_dict.get("variation"),
            date=json_dict.get("date")
        )

    def print_fields(self):
        print("company: ",self.company, ";",
              "value: ",self.value, ";",
              "drop: ",self.drop, ";",
              "variation: ",self.variation, ";",
              "date: ",self.date)
