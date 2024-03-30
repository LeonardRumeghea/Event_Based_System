import datetime
import json


class Publication:
    def __init__(self, company: str, value: float, drop: float, variation: float, date: datetime.date):
        self.company = company
        self.value = value
        self.drop = drop
        self.variation = variation
        self.date = date

    def to_json(self):
        json_dict = {'company': self.company, 'value': self.value, 'drop': self.drop, 'variation': self.variation,
                     'date': self.date}
        return json.dumps(json_dict, indent=4)

    @staticmethod
    def from_json(json):
        return Publication(
            json['company'],
            json['value'],
            json['drop'],
            json['variation'],
            datetime.datetime.strptime(json['date'], '%d-%m-%Y').date()
        )
