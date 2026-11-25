//
//  ShareHelper.swift
//  RickMortyApp
//

import UIKit
import RickMortyKMP

enum ShareHelper {
  static func share(_ character: RickMortyKMP.Character) {
    let text = "Olha esse personagem de Rick and Morty: \(character.name) (\(character.species))"
    let url = URL(string: character.image)!
    let activity = UIActivityViewController(activityItems: [text, url], applicationActivities: nil)
    UIApplication.shared.windows.first!.rootViewController!.present(activity, animated: true)
  }
}
