import datetime

class Publication:
    def __init__(self, companie: str, value: float, drop: float, variation: float, date: datetime.date):
        self.companie = companie
        self.value = value
        self.drop = drop
        self.variation = variation
        self.date = date

    def to_json(self):
        return {
            'companie': self.companie,
            'value': self.value,
            'drop': self.drop,
            'variation': self.variation,
            'date': self.date
        }
    
    @staticmethod
    def from_json(json):
        return Publication(
            json['companie'],
            json['value'],
            json['drop'],
            json['variation'],
            datetime.datetime.strptime(json['date'], '%d-%m-%Y').date()
        )
