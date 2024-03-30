import datetime

class Subscription:
    def __init__(self, companie: str, value: float, drop: float, variation: float, date: datetime.date):
        self.companie = companie
        self.value = value
        self.drop = drop
        self.variation = variation
        self.date = date

    def to_json(self):
        # create a json object from the object with the attributes that are not None
        # return the json object
        json = {}
        if self.companie:
            json['companie'] = self.companie

        if self.value:
            json['value'] = self.value

        if self.drop:
            json['drop'] = self.drop

        if self.variation:
            json['variation'] = self.variation

        if self.date:
            json['date'] = self.date.strftime('%d-%m-%Y')

        return json
    
    @staticmethod
    def from_json(json):
        # create a new object from the json object
        # return the new object
        return Subscription(
            json['companie'],
            json['value'],
            json['drop'],
            json['variation'],
            datetime.datetime.strptime(json['date'], '%d-%m-%Y').date()
        )
