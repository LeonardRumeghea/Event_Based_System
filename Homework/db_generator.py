import subscription, publication
import math
import json

OUTPUT_FILE = './result.json'

number_of_publications = 150_000
number_of_subscriptions = 10
companies = ['Apple', 'Google', 'Microsoft', 'Amazon', 'Facebook', 'Netflix', 'Tesla', 'IBM', 'Oracle', 'Intel']
comparison_signs = ['=', '!=', '>', '>=', '<', '<=']

# will take them as input from keyboard
company_percentage = 60
value_percentage = 30
drop_percentage = 40
variation_percentage = 0
date_percentage = 0

equal_sight_freq = 70
company_percentage_equal_sight = math.ceil(
    equal_sight_freq / 100 * math.ceil(company_percentage / 100 * number_of_subscriptions))


def generate_subs_structure():
    nr_company = math.ceil(company_percentage / 100 * number_of_subscriptions)
    nr_value = math.ceil(value_percentage / 100 * number_of_subscriptions)
    nr_drop = math.ceil(drop_percentage / 100 * number_of_subscriptions)
    nr_variation = math.ceil(variation_percentage / 100 * number_of_subscriptions)
    nr_date = math.ceil(date_percentage / 100 * number_of_subscriptions)

    total_fields = nr_company + nr_value + nr_drop + nr_variation + nr_date
    list_of_subscriptions = [subscription.Subscription() for _ in range(number_of_subscriptions)]
    while total_fields != 0:
        for i in range(number_of_subscriptions):
            if total_fields == 0:
                break
            if nr_company != 0:
                list_of_subscriptions[i].company = []
                nr_company -= 1
                total_fields -= 1
            elif nr_value != 0:
                list_of_subscriptions[i].value = []
                nr_value -= 1
                total_fields -= 1
            elif nr_drop != 0:
                list_of_subscriptions[i].drop = []
                nr_drop -= 1
                total_fields -= 1
            elif nr_variation != 0:
                list_of_subscriptions[i].variation = []
                nr_variation -= 1
                total_fields -= 1
            elif nr_date != 0:
                list_of_subscriptions[i].date = []
                nr_date -= 1
                total_fields -= 1
    return list_of_subscriptions


# convert percentage to number - 80% of 10_000 = 8_000

# split the numbers for each thread

# run the threads

# join the results

# Tested the json part
# subscri = subscription.Subscription(company=["!=", "google"], value=[">=", 30], variation=["<", 0.8])
# new_sub = subscription.Subscription.from_json(subscri.to_json())
# with open(OUTPUT_FILE, "w") as json_file:
#     json_file.write(json.dumps([subscri.to_json(), new_sub.to_json()]))

list_of_subs = generate_subs_structure()
for i in range(number_of_subscriptions):
    list_of_subs[i].print_fields()
