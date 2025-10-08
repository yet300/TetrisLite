//
//  MutableValue.swift
//  iosApp
//
//  Created by Ruslan Yet on 08.10.25.
//
import Shared

func mutableValue<T: AnyObject>(_ initialValue: T) -> MutableValue<T> {
    return MutableValueBuilderKt.MutableValue(initialValue: initialValue) as! MutableValue<T>
}
