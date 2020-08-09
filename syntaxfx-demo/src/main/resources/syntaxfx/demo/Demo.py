"""
This class represents a Vehicle object.
"""
class Vehicle:
    def __init__(self, brand, model, type):
        self.brand = brand
        self.model = model
        self.type = type
        self.gas_tank_size = 14
        self.fuel_level = 0

"""
Fills up the vehicle's gas tank.
"""
    def fuel_up(self):
        self.fuel_level = self.gas_tank_size
        print('Gas tank is now full.')

"""
Drives the vehicle.
"""
    def drive(self):
        print(f'The {self.model} is now driving.')

"""
Sets the fuel level of the vehicle to the given level.
"""
    def update_fuel_level(self, new_level):
        if new_level <= self.gas_tank_size:
            self.fuel_level = new_level
        else:
            print('Exceeded capacity')

# Create a new Vehicle
mycar = Vehicle("Ford", "Taurus", "SE")
# Perform various actions with the vehicle
mycar.fuel_up()
mycar.drive()
mycar.update_fuel_level(12)
# Print the vehicle's fuel level
print(mycar.fuel_level)
