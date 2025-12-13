//
//  CharacterRow.swift
//  RickMortyApp
//
//  Created by Fernando Sales on 13/12/25.
//

import SwiftUI
import RickMortyKMP

struct CharacterRow: View {
  let character: RickMortyKMP.Character
  
  var body: some View {
    HStack {
      AsyncImage(url: URL(string: character.image)) { phase in
        switch phase {
          case .empty:
            ProgressView()
              .frame(width: 80, height: 80)
          case .success(let image):
            image
              .resizable()
              .aspectRatio(contentMode: .fill)
              .frame(width: 80, height: 80)
              .clipShape(RoundedRectangle(cornerRadius: 12))
          case .failure:
            Image("photo")
              .resizable()
              .aspectRatio(contentMode: .fill)
              .frame(width: 80, height: 80)
              .clipShape(RoundedRectangle(cornerRadius: 12))
          @unknown default:
            EmptyView()
        }
      }
      
      VStack(alignment: .leading, spacing: 6) {
        Text(character.name)
          .font(.headline)
          .lineLimit(1)
        
        HStack(spacing: 6) {
          Circle()
            .fill(statusColor)
            .frame(width: 10, height: 10)
          
          Text("\(character.status) - \(character.species)")
            .font(.subheadline)
            .foregroundStyle(.secondary)
            .lineLimit(1)
        }
      }
      
      Spacer()
    }
    .padding(.vertical, 4)
  }
  
  private var statusColor: Color {
    switch character.status {
      case "Alive": return .green
      case "Dead": return .red
      default: return .gray
    }
  }
}

#Preview {
  CharacterRow(character: Character(id: 1, name: "Rick", status: "Alive", species: "Human", image: "https://rickandmortyapi.com/api/character/avatar/1.jpeg"))
}
