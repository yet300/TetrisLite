//
//  StateValue.swift
//  iosApp
//
//  Created by Ruslan Yet on 08.10.25.
//


import SwiftUI
import Shared

@propertyWrapper struct StateValue<T : AnyObject>: DynamicProperty {
    @ObservedObject
    private var obj: ObservableValue<T>

    var wrappedValue: T { obj.value }

    init(_ value: Value<T>) {
        obj = ObservableValue(value)
    }
}
